package com.company;

import java.util.*;

// |X.2 Linked Lists solutions

public class LinkedLists {

    // helper functions
    public static Node createRandomLinkedList(int n) {
        if (n <= 0) return null;

        Random rand = new Random();
        Node head = new Node(rand.nextInt(10)); // random int between 0–9

        for (int i = 1; i < n; i++) {
            head.appendToTail(rand.nextInt(10));
        }
        return head;
    }

    public static Node createListFromArray(int[] arr) {
        if (arr == null || arr.length == 0) {
            return null;
        }

        Node head = new Node(arr[0]);
        Node current = head;

        for (int i = 1; i < arr.length; i++) {
            current.next = new Node(arr[i]);
            current = current.next;
        }

        return head;
    }

    public static void printLinkedList(Node linkedList) {
        Node head = linkedList;
        while (head != null) {
            System.out.print(head.data + " ");
            head = head.next;
        }
        System.out.println();
    }

    public static void swapNodes(Node a, Node b) {
        int temp = a.data;
        a.data = b.data;
        b.data = temp;
    }

    public static Node[] createIntersectingLists() {

        // Shared tail (intersection)
        Node shared = new Node(8);
        shared.next = new Node(10);

        // List 1: 3 → 7 → 8 → 10
        Node l1 = new Node(3);
        l1.next = new Node(7);
        l1.next.next = shared;

        // List 2: 99 → 1 → 8 → 10
        Node l2 = new Node(99);
        l2.next = new Node(1);
        l2.next.next = shared;

        return new Node[] { l1, l2 };
    }

    public static Node createListWithLoop(int[] values, int loopIndex) {
        if (values == null || values.length == 0) return null;

        Node head = new Node(values[0]);
        Node curr = head;

        Node loopNode = null;
        if (loopIndex == 0) loopNode = head;

        for (int i = 1; i < values.length; i++) {
            curr.next = new Node(values[i]);
            curr = curr.next;

            if (i == loopIndex) {
                loopNode = curr;
            }
        }

        // create the loop
        if (loopNode != null) {
            curr.next = loopNode;
        }

        return head;
    }


    // 2.1: function to remove duplicate values from unordered linked list
    public static void removeDuplicate(Node l) {
        if (l == null) return;
        HashSet<Integer> s = new HashSet<>();
        Node head = l;
        s.add(head.data);

        while (head != null && head.next != null) {
            if (s.contains(head.next.data)) {
                head.next = head.next.next;
            } else s.add(head.next.data);
            head = head.next;
        }
    }

    // 2.2 print kth to last ( start counting from last -> return the kth element from last )
    public static int printKthToLast(Node head, int k) {
        // getting the kth from last without knowing the size
        if (head == null) return 0;
        int index = printKthToLast(head.next, k) + 1;
        if (index == k) System.out.println(head.data);
        return index;
    }

    // 2.3 delete a middle node ( function gets a ref to the node it will delete -not the head of linked-list )
    // pre: the node is not the last node
    public static boolean deleteMiddleNode(Node head) {
        if (head == null || head.next == null) return false;
        head.data = head.next.data;
        head.next = head.next.next;
        return true;
    }

    // 2.4 partition linkedlist -> group all elements lower than a threshold x at the beginning of the linked list
    public static void partition(Node head, int x) {
        Node l = head;
        while (l != null && l.data < x) l = l.next;
        Node r = l;
        while (r != null && r.data >= x) r = r.next;

        while (r != null) {
            swapNodes(l, r);
            do r = r.next;
            while (r != null && r.data >= x);
            do l = l.next;
            while (l != r && l.data < x);
        }
    }

//    2.5 add 2 integer numbers where the digits are listed in linked list
    public static Node addLists(Node f, Node s){
        Node result = new Node(-1);
        int r = 0;
        while(f!=null || s!=null){
            int fd = 0, sd = 0;
            if(f!=null){
                fd = f.data;
                f = f.next;
            }
            if(s!=null){
                sd = s.data;
                s = s.next;
            }
            int data = fd + sd + r;
            result.appendToTail(data%10);
            r = data/10;
        }
        if(r != 0) result.appendToTail(r);
        return result.next;
    }

//  2.6 return true if the linked list is palindrome
    public static  boolean isPalindrome(Node list){
        Node ps = list, pf = list;
        Stack<Integer> st = new Stack<>();
        while(pf != null){
            if(pf.next == null){
                ps = ps.next;
                break;
            }
            st.push(ps.data);
            ps = ps.next;
            pf = pf.next.next;
        }
        // here lenght(ps) = lenght(st)
        while(ps != null){
            if(ps.data != st.pop()) return false;
            ps = ps.next;
        }
        return true;
    }

//    2.7: return the intersection node if exists otherwise return null
    public static Node findintersection(Node l1, Node l2){
        if(l1==null || l2==null) return null;
        Node h1 = l1, h2 = l2;
        Node t1 = h1, t2 = h2;
        int sz1 = 1, sz2 = 1;

//        calc. size and get last node of each list
        while(t1.next != null){
            t1 = t1.next;
            sz1++;
        }
        while(t2.next != null){
            t2 = t2.next;
            sz2++;
        }

        if(t1 != t2) return null; // last node is not equal (so no intersection)

        //        move biggest list the part of the node that is diff
        if(sz2 > sz1){
            h1 = l2; h2 = l1;
        }
        int diff = Math.abs(sz1 - sz2);
        while(diff-- != 0 && h1 != null) h1 = h1.next; // h1 can't be null here but for the lint
        while(h1!=h2){
            h1 = h1.next;
            h2 = h2.next;
        }

        return h1;
    }

    // 2.8: detect loop in the linkedlist and return the first node of the loop
    public static Node findLoopBeginning(Node list){
        Node f = list, s = list;
        while(f!=null && f.next!=null){
            s=s.next;
            f = f.next.next; // will cause error
            if(f==s){ // first collision
                break;
            }
        }
        if(f==null || f.next==null){
            return null;
        }
        f = list;
        while(f!=s) { // loop untill second collision (at the beginning of the loop
            f = f.next; s = s.next;
        }
        return f;
    }
    public static void main(String[] args) {
        // 2.1 testing
        Node linkedList = createListFromArray(new int[] {0,1,2,1,1,0});
        System.out.println(isPalindrome(linkedList));
        System.out.println(linkedList);
        System.out.println(new Node(2));

//        testing intersection
        Node[] lists = createIntersectingLists();

        Node intersection = findintersection(lists[0], lists[1]);
        printLinkedList(lists[0]);
        printLinkedList(lists[1]);
        System.out.println(intersection != null
                ? intersection.data
                : "No intersection");

//        testing loop detection
        Node listWithLoop = createListWithLoop(
                new int[]{10, 20, 30, 40, 50},
                0
        );
        Node loopHead = findLoopBeginning(listWithLoop);
        System.out.println(loopHead != null
                ? loopHead.data
                : "No Loop");
    }
}

class Node {
    Node next = null;
    int data;

    public Node(int d) {
        data = d;
    }

    void appendToTail(int n) { //  very bad, the node shouldn't know it is in the list
        Node newNode = new Node(n);
        Node head = this;
        while (head.next != null) {
            head = head.next;
        }
        head.next = newNode;
    }

}

