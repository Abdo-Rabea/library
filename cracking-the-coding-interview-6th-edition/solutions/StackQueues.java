
//    |X.3: Stack and Queues chapter
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Stack;

public class StackQueues {
    // 3.1: Three in one: imp. 3 stacks using single array (just read - the flex
    // design is a lot of headache)

    // 3.2: Stack Min: design a stack that supports push, pop, and min in O(1) time
    // (use 2 stacks - one for data, one for mins)
    public static class NodeWithMin<T> {
        public T value;
        public T min;

        public NodeWithMin(T value, T min) {
            this.value = value;
            this.min = min;
        }

    }

    public static class StackMin<T> {
        Stack<NodeWithMin<T>> stack = new Stack<>();

        public void push(T value) {
            if (stack.isEmpty()) {
                stack.push(new NodeWithMin<>(value, value));
            } else {
                T currentMin = this.min();
                T newMin = (currentMin instanceof Comparable)
                        ? ((Comparable<T>) currentMin).compareTo(value) < 0 ? currentMin : value
                        : currentMin; // if not comparable, just keep current min
                stack.push(new NodeWithMin<>(value, newMin));
            }
        }

        public T pop() {
            if (stack.isEmpty())
                throw new RuntimeException("Stack is empty");
            return stack.pop().value;
        }

        public T min() {
            if (stack.isEmpty())
                throw new RuntimeException("Stack is empty");
            return stack.peek().min;
        }
    }

    // 3.3 Stack of Plates: implement a stack that creates new stacks when previous
    // one exceeds capacity
    // TODO: impelement popAt(index) that pops from a specific sub-stack
    public static class SetOfStacks<T> {
        ArrayList<Stack<T>> stacks = new ArrayList<>();
        final static int stackCapacity = 5;

        private int getIndexOfLastStack() {
            return stacks.size() - 1;
        }

        public void push(T value) {
            int index = getIndexOfLastStack();
            if (index == -1 || stacks.get(index).size() >= stackCapacity) {
                Stack<T> newStack = new Stack<>();
                newStack.push(value);
                stacks.add(newStack);
            } else {
                stacks.get(index).push(value);
            }
        }

        public T pop() {
            int index = getIndexOfLastStack();
            if (index == -1)
                throw new RuntimeException("No stacks available");
            Stack<T> lastStack = stacks.get(index);
            T value = lastStack.pop();
            if (lastStack.isEmpty()) {
                stacks.remove(index);
            }
            return value;
        }
    }

    // 3.4: Queue via Stacks: implement a queue using two stacks
    public static class MyQueue<T> {
        Stack<T> popStack, pushStack;

        public MyQueue() {
            popStack = new Stack<>();
            pushStack = new Stack<>();
        }

        public void enqueue(T value) {
            pushStack.push(value);
        }

        public T dequeue() {
            if (popStack.isEmpty() && pushStack.isEmpty()) {
                throw new RuntimeException("Queue is empty");
            }
            if (popStack.isEmpty()) {
                while (!pushStack.isEmpty()) {
                    popStack.push(pushStack.pop());
                }
            }
            return popStack.pop();
        }
    }

    // 3.5: Sort Stack: sort a stack such that the smallest items are on the top
    // using only one additional stack
    static void sortStack(Stack<Integer> s) {
        Stack<Integer> r = new Stack<>();
        while (!s.isEmpty()) {
            int temp = s.pop();
            while (!r.isEmpty() && r.peek() > temp) {
                s.push(r.pop());
            }
            r.push(temp);
        }
        while (!r.isEmpty()) {
            s.push(r.pop());
        }
    }

    // 3.6: Animal Shelter: implement a data structure for an animal shelter.
    static class Animal {
        String name;
        int order; // lower order means older

        Animal(String name) {
            this.name = name;
        }

        public int getOrder() {
            return order;
        }

        public void setOrder(int order) {
            this.order = order;
        }

        public String getName() {
            return name;
        }
    }

    static class Dog extends Animal {
        Dog(String name) {
            super(name);
        }
    }

    static class Cat extends Animal {
        Cat(String name) {
            super(name);
        }
    }

    public static class AnimalShelter {
        private LinkedList<Dog> dogs = new LinkedList<>();
        private LinkedList<Cat> cats = new LinkedList<>();
        private int order = 0;

        public void enqueue(Animal animal) {
            animal.setOrder(order++);
            if (animal instanceof Dog) {
                dogs.add((Dog) animal);
            } else if (animal instanceof Cat) {
                cats.add((Cat) animal);
            }
        }

        public Animal dequeueAny() {
            if (dogs.isEmpty() && cats.isEmpty()) {
                throw new RuntimeException("No animals available");
            } else if (dogs.isEmpty()) {
                return cats.poll();
            } else if (cats.isEmpty()) {
                return dogs.poll();
            } else {
                Dog dog = dogs.peek();
                Cat cat = cats.peek();
                if (dog.getOrder() < cat.getOrder()) {
                    return dogs.poll();
                } else {
                    return cats.poll();
                }
            }
        }

        public Dog dequeueDog() {
            if (dogs.isEmpty()) {
                throw new RuntimeException("No dogs available");
            }
            return dogs.poll();
        }

        public Cat dequeueCat() {
            if (cats.isEmpty()) {
                throw new RuntimeException("No cats available");
            }
            return cats.poll();
        }
    }

    public static void main(String[] args) {
        // StackMin<Integer> st = new StackMin<>();
        // ArrayList<Integer> nums = new ArrayList<>();
        // System.out.println(nums.size());
        // test stack of plates
        // SetOfStacks<Integer> setOfStacks = new SetOfStacks<>();
        // for (int i = 0; i < 12; i++) {
        // setOfStacks.push(i);
        // }
        // for (int i = 0; i < 12; i++) {
        // System.out.println(setOfStacks.pop());
        // }
        // test MyQueue
        // MyQueue<Integer> myQueue = new MyQueue<>();
        // for (int i = 0; i < 5; i++) {
        // myQueue.enqueue(i);
        // }

        // for (int i = 0; i < 4; i++) {
        // System.out.println(myQueue.dequeue());
        // }
        // myQueue.enqueue(10);
        // myQueue.enqueue(6);
        // System.out.println(myQueue.dequeue());
        // System.out.println(myQueue.dequeue());
        // System.out.println(myQueue.dequeue());
        // System.out.println(myQueue.dequeue());
        // test sort stack
        // Stack<Integer> s = new Stack<>();
        // s.push(3);
        // s.push(1);
        // s.push(4);
        // s.push(2);
        // // print before sorting
        // System.out.println("Before sorting:");
        // for (Integer i : s) {
        // System.out.println(i);
        // }
        // sortStack(s);
        // // print after sorting
        // System.out.println("After sorting:");
        // for (Integer i : s) {
        // System.out.println(i);
        // }

        // test animal shelter
        AnimalShelter shelter = new AnimalShelter();
        shelter.enqueue(new Dog("Rex"));
        shelter.enqueue(new Cat("Whiskers"));
        shelter.enqueue(new Cat("Mittens"));
        shelter.enqueue(new Dog("Buddy"));
        System.out.println("Dequeue Any: " + shelter.dequeueAny().getName()); // Rex
        System.out.println("Dequeue Dog: " + shelter.dequeueDog().getName()); // Buddy
        System.out.println("Dequeue Cat: " + shelter.dequeueCat().getName()); // Whiskers

    }
}
