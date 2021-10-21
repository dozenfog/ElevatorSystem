package by.issoft.controller;

import by.issoft.enums.Direction;
import by.issoft.enums.ElevatorCondition;
import by.issoft.generator.PeopleSpawn;
import by.issoft.model.elevator.Elevator;
import by.issoft.model.elevator.ElevatorTask;
import by.issoft.model.people.Crowd;
import by.issoft.model.people.Person;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

@Getter
public class ElevatorController extends Thread {
    private final BlockingQueue<Crowd> sharedCrowdsQueue;
    private final List<Elevator> elevators;
    public static final int REQUEST_TIMEOUT_IN_SECONDS = 10;
    public int gateOpeningTimeInSeconds = 1;
    public int managingPeopleTimeInSeconds = 1;
    private static final Logger logger = LoggerFactory.getLogger("elevatorControllerLogger");
    private boolean exit;

    public ElevatorController(BlockingQueue<Crowd> sharedCrowdsQueue, List<Elevator> elevators) {
        checkParams(sharedCrowdsQueue, elevators);
        this.sharedCrowdsQueue = sharedCrowdsQueue;
        this.elevators = Collections.synchronizedList(elevators);
        this.elevators.forEach(elevator -> {
            elevator.setGateOpeningTimeInSeconds(gateOpeningTimeInSeconds);
            elevator.setManagingPeopleTimeInSeconds(managingPeopleTimeInSeconds);
            elevator.start();
        });
        this.exit = false;
    }

    public ElevatorController(BlockingQueue<Crowd> sharedCrowdsQueue, List<Elevator> elevators,
                              int gateOpeningTimeInSeconds, int managingPeopleTimeInSeconds) {
        this(sharedCrowdsQueue, elevators);
        checkArgument(gateOpeningTimeInSeconds > 0, "Gate opening time should be positive!");
        checkArgument(managingPeopleTimeInSeconds > 0, "Managing people time should be positive!");
        this.elevators.forEach(elevator -> {
            elevator.setGateOpeningTimeInSeconds(gateOpeningTimeInSeconds);
            elevator.setManagingPeopleTimeInSeconds(managingPeopleTimeInSeconds);
        });
    }

    private void checkParams(BlockingQueue<Crowd> sharedCrowdsQueue, List<Elevator> elevators) {
        checkNotNull(sharedCrowdsQueue);
        checkNotNull(elevators);
        checkArgument(!elevators.isEmpty(), "There should be at least 1 elevator!");
    }

    @Override
    public void run() {
        try {
            List<CompletableFuture<Void>> completableFutureList = new ArrayList<>();
            while (!exit) {
                while (!sharedCrowdsQueue.isEmpty()) {
                    Crowd curCrowd = sharedCrowdsQueue.take();
                    logger.debug("Task was taken from queue with crowd on floor " + curCrowd.getFloor()
                            + " riding " + curCrowd.getDirection());
                    Thread.sleep(1000);
                    CompletableFuture<Void> completableFuture = processCurCrowdInBackground(curCrowd);
                    completableFutureList.add(completableFuture);
                }
            }
            waitForFuturesToComplete(completableFutureList);
            stopElevators();
            logger.debug("NO MORE TASKS TO BE PROCESSED, WAITING FOR ELEVATORS TO FINISH THEIR TASKS");
            waitUntilTasksFinished();
        } catch (InterruptedException | ExecutionException e) {
            System.out.println(e.getMessage());
        }
        logger.debug("Controller has been stopped");
    }

    private CompletableFuture<Void> processCurCrowdInBackground(Crowd curCrowd) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                do {
                    List<Elevator> freeElevators = findElevatorsForRequest(curCrowd);
                    while (freeElevators.size() == 0) {
                        logger.debug("Crowd on floor " + curCrowd.getFloor() + " is waiting for available elevators");
                        Thread.sleep(REQUEST_TIMEOUT_IN_SECONDS * 1000);
                        freeElevators = findElevatorsForRequest(curCrowd);
                    }
                    logElevatorIsFound(freeElevators, curCrowd);
                    sendTasksToElevators(curCrowd, freeElevators);
                } while (!curCrowd.getPeople().isEmpty());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "Crowd on floor " + curCrowd.getFloor() + " has been processed";
        }).thenAccept(logger::debug);
    }

    private void waitForFuturesToComplete(List<CompletableFuture<Void>> completableFutureList)
            throws ExecutionException, InterruptedException {
        int size = completableFutureList.size();
        CompletableFuture<Void> completableFuture =
                CompletableFuture.allOf(completableFutureList.toArray(new CompletableFuture[size]))
                        .thenRun(() -> logger.debug("ALL FUTURES HAVE COMPLETED EXECUTION"));
        completableFuture.get();
    }

    private void stopElevators() {
        for (Elevator elevator : elevators) {
            elevator.stopElevator();
        }
    }

    private void waitUntilTasksFinished() throws InterruptedException {
        for (Elevator elevator : elevators) {
            if (elevator.getTaskQueue().isEmpty()) {
                elevator.stopElevator();
            } else {
                elevator.join();
            }
        }
    }

    private synchronized void logElevatorIsFound(List<Elevator> freeElevators, Crowd curCrowd) {
        logger.debug(freeElevators.stream()
                .map(Thread::getName)
                .collect(Collectors.joining(", ")) + " have been found for crowd on floor "
                + curCrowd.getFloor() + " riding " + curCrowd.getDirection());
    }

    private synchronized List<Elevator> findElevatorsForRequest(Crowd crowd) {
        double finalMinWeightInCrowd = countMinWeightInCrowd(crowd);
        return elevators.stream()
                .filter(elevator -> checkElevatorWaitingEmpty(elevator) || checkElevatorRidingTowardsCrowd(elevator, crowd))
                .filter(elevator -> checkElevatorHasExcessSpace(elevator, finalMinWeightInCrowd))
                .sorted(getElevatorComparatorByDistanceAndTaskNumber(crowd.getFloor()))
                .collect(Collectors.toList());
    }

    private synchronized double countMinWeightInCrowd(Crowd crowd) {
        double minWeightInCrowd = PeopleSpawn.MIN_PEOPLE_WEIGHT;
        Optional<Person> person = crowd.getPeople().stream().min(Comparator.comparing(Person::getWeightInKg));
        if (person.isPresent()) {
            minWeightInCrowd = person.get().getWeightInKg();
        }
        return minWeightInCrowd;
    }

    private synchronized boolean checkElevatorWaitingEmpty(Elevator elevator) {
        return elevator.getCondition() == ElevatorCondition.WAITING_EMPTY;
    }

    private synchronized boolean checkElevatorRidingTowardsCrowd(Elevator elevator, Crowd crowd) {
        return (elevator.getDirection().equals(crowd.getDirection()) &&
                Math.signum(crowd.getFloor() - elevator.getPositionOfLastCheckpoint().get())
                        == elevator.getDirection().getSign());
    }

    private synchronized boolean checkElevatorHasExcessSpace(Elevator elevator, double finalMinWeightInCrowd) {
        return elevator.getLiftingCapacityInKg() - elevator.getTakenWeightInKg() >= finalMinWeightInCrowd;
    }

    private synchronized Comparator<Elevator> getElevatorComparatorByDistanceAndTaskNumber(int neededFloor) {
        return Comparator.comparing((Elevator elevator) -> elevator.getTaskQueue().size())
                .thenComparing(Elevator::getCondition, Comparator.reverseOrder())
                .thenComparing(elevator -> Math.abs(elevator.getPositionOfLastCheckpoint().get() - neededFloor));
    }

    private synchronized void sendTasksToElevators(Crowd crowd, List<Elevator> freeElevators) throws InterruptedException {
        int elevatorIndex = 0;
        do {
            if (elevatorIndex != 0) {
                logger.debug("Not all people from level " + crowd.getFloor() + " were able to fit into elevator " +
                        freeElevators.get(elevatorIndex - 1).getNumber() + ", trying next " +
                        freeElevators.get(elevatorIndex).getName());
            }
            Elevator elevator = freeElevators.get(elevatorIndex);
            List<Person> chosenPeople = choosePeopleForElevator(crowd, elevator);
            addEnterElevatorTask(chosenPeople, elevator);
            Thread.sleep(100);
            addExitElevatorTasks(chosenPeople, elevator);
            elevatorIndex++;
        } while (!crowd.getPeople().isEmpty() && freeElevators.size() > elevatorIndex);
    }

    private synchronized List<Person> choosePeopleForElevator(Crowd crowd, Elevator elevator) {
        List<Person> chosenPeople = new ArrayList<>(crowd.getPeople());
        if (crowd.getOverallWeight() <= elevator.getLiftingCapacityInKg() - elevator.getTakenWeightInKg()) {
            crowd.getPeople().clear();
        } else {
            chosenPeople = choosePeople(crowd.getPeople(),
                    elevator.getLiftingCapacityInKg() - elevator.getTakenWeightInKg());
            crowd.getPeople().removeAll(chosenPeople);
        }
        return chosenPeople;
    }

    private synchronized List<Person> choosePeople(List<Person> people, double capacity) {
        List<Person> chosenPeople = new ArrayList<>();
        List<Person> sortedPeople = people.stream()
                .sorted(Comparator.comparing(Person::getWeightInKg))
                .collect(Collectors.toList());
        double reachedWeight = 0;
        int index = 0;
        Person curPerson;
        do {
            curPerson = sortedPeople.get(index);
            chosenPeople.add(curPerson);
            reachedWeight += curPerson.getWeightInKg();
            index++;
        } while (reachedWeight + curPerson.getWeightInKg() <= capacity && sortedPeople.size() > index);
        return chosenPeople;
    }

    private synchronized Optional<ElevatorTask> getIfElevatorTaskExists(int floor, Elevator elevator) {
        Optional<ElevatorTask> optionalElevatorTask = elevator.getTaskQueue().stream()
                .filter(elevatorTask -> elevatorTask.getNeededFloor() == floor)
                .findFirst();
        if (optionalElevatorTask.isEmpty() && elevator.getCurTask() != null
                && elevator.getCurTask().getNeededFloor() == floor) {
            optionalElevatorTask = Optional.ofNullable(elevator.getCurTask());
        }
        return optionalElevatorTask;
    }

    private synchronized void addEnterElevatorTask(List<Person> chosenPeople, Elevator elevator) {
        int initialFloor = chosenPeople.get(0).getInitialFloor();
        Optional<ElevatorTask> optionalElevatorTask = getIfElevatorTaskExists(initialFloor, elevator);
        if (optionalElevatorTask.isPresent()) {
            optionalElevatorTask.get().getListOnEnter().addAll(chosenPeople);
            logger.debug("Task (get people from floor:" + initialFloor + ") for elevator " +
                    elevator.getNumber() + " has been updated");
        } else {
            ElevatorTask chosenPeopleEnterTask = new ElevatorTask(initialFloor);
            chosenPeopleEnterTask.getListOnEnter().addAll(chosenPeople);
            elevator.getTaskQueue().add(chosenPeopleEnterTask);
            logger.debug("Task (get people from floor:" + initialFloor + ") for elevator " +
                    elevator.getNumber() + " has been added to queue");
        }
    }

    private synchronized void addExitElevatorTasks(List<Person> chosenPeople, Elevator elevator) {
        Map<Integer, List<Person>> groupedByFloor = groupByFloor(chosenPeople);
        groupedByFloor.keySet().forEach(floor -> {
            Optional<ElevatorTask> foundElevatorTask =
                    getIfElevatorTaskExists(floor, elevator);
            if (foundElevatorTask.isEmpty()) {
                createNewElevatorTask(groupedByFloor.get(floor), elevator, floor);
                logger.debug("Task (drop off people to floor:" + floor +
                        ") for elevator " + elevator.getNumber() + " has been added to queue");
            } else {
                updateElevatorTask(foundElevatorTask.get(), groupedByFloor.get(floor));
                logger.debug("Task (drop off people to floor:" + floor +
                        ") for elevator " + elevator.getNumber() + " has been updated");
            }
        });
    }

    private synchronized Map<Integer, List<Person>> groupByFloor(List<Person> chosenPeople) {
        Direction direction = chosenPeople.get(0).getDirection();
        Map<Integer, List<Person>> collectedByFloor = chosenPeople.stream()
                .collect(Collectors.groupingBy(Person::getNeededFloor));
        Map<Integer, List<Person>> groupedByFloor = new TreeMap<>();
        if (direction == Direction.DOWN) {
            groupedByFloor = new TreeMap<>(Collections.reverseOrder());
        }
        groupedByFloor.putAll(collectedByFloor);
        return groupedByFloor;
    }

    private synchronized void createNewElevatorTask(List<Person> groupedByFloor, Elevator elevator, Integer floor) {
        ElevatorTask task = new ElevatorTask(floor);
        task.getListOnExit().addAll(groupedByFloor);
        elevator.getTaskQueue().add(task);
    }

    private synchronized void updateElevatorTask(ElevatorTask foundElevatorTask, List<Person> groupedByFloor) {
        foundElevatorTask.getListOnExit().addAll(groupedByFloor);
    }

    public void stopController() {
        exit = true;
    }
}
