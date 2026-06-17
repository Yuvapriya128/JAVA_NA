package producerConsumer;

public class MainPC {
    public static void main(String[] args) {
        Inventory inventory=new Inventory();

//        create producer
        Thread producer=new Thread(()->{
            int i=1;
            while(i<=100){
//                lock the inventory,
//                before unlocking, notify
                synchronized (inventory){
                    while(inventory.size()>=10){
                        System.out.println("Inventory is full, producer going to sleep");
                        try {
                            inventory.wait();
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    try {
                        inventory.add("item_"+i);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    System.out.println("Producer produced: item_"+i);
                    i++;
                    inventory.notify();
                }

            }
        });

//        create consumer
        Thread consumer=new Thread(()->{
            int i=1;
            while(i<=100){
                synchronized (inventory){
                    while(inventory.size()<=0){
                        System.out.println("Inventory is empty, consumer going to sleep");
                        try {
                            inventory.wait();
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    try {
                        inventory.remove();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    System.out.println("Consumer consumed item_"+i);
                    i++;
                    inventory.notify();
                }

            }

        });
        producer.start();

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        consumer.start();

    }
}
