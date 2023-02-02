# Алгоритм работы системы
В проекте ключевыми являются классы Building, Floor, Elevator, Elevator Controller. Был реализован паттерн Consumer-Producer, так что Elevator Controller является Consumer по отношению к Floor (который Producer) и Producer по отношению к Elevators (они Consumers). 
Разделяемый ресурс- Crowd между Elevator Controller и Floor и Elevator Task между Elevator и Elevator Controller. При генерации Crowds в Floors нажимаются Buttons, затем они отпускаются согласно этому же паттерну между Elevator (Producer) и ButtonSwitchDistributor (Consumer) в качестве Button Switch Message.
Паттерн реализован с помощью Blocking Queues.
## Алгоритм подбора лифтов
Многоступенчатая сортировка:
1. Проверка на положение лифта относительно очереди
2. Проверка на наличие свободного места
3. Сортировка лифтов по количеству Elevator Task и близости к очереди
## Алгоритм обработки очередей
Очереди поступают в sharedCrowdsQueue, Elevator Controller обрабатывает их параллельно используя Completable Future. Он ищет лифты для очереди и формирует из очередей Elevator Tasks, которые потом отправляются назначенным лифтам.
Лифты получают Elevator tasks из TaskQueue, обрабатывают их последовательно. По приезду на этаж подают Button Switch Message в ButtonSwitchDistributor. Также обновляется статистика.

Floors непрерывно генерируют очереди с таймаутом, который выбирается рандомно между 1 и maxTimeoutBetweenGenerations.
Каждый этап логгируется (в файлы и на консоль) и можно посчитать что нужно, поэтому в статистике представлена только общая сводка.
