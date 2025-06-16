package com.company;

import java.util.Arrays;
import java.util.HashMap;

//    x.1 Solutions to Arrays and Strings
public class ArraysAndStrings {

    // 1.1: check if string consists of unique chars
    public static boolean hasUniqueChar(String s) {

        HashMap<Character, Integer> mp = new HashMap<>();
        for (char c : s.toCharArray()) {
            if (mp.get(c) != null) return false;
            mp.put(c, 1);
        }
        return true;
    }

    // solution 1.2: check if the 2 strings are permutations of each others or not
    public static boolean isPermutation(String s, String t) {
        if (s.length() != t.length()) return false;
        int[] letterFreq = new int[126];

        for (char c : s.toCharArray()) {
            letterFreq[c]++;
        }

        for (char c : t.toCharArray()) {
            if (--letterFreq[c] < 0) return false;
        }

        return true;
    }

    // solution 1.3 URLify
    //    Input:  "Mr John Smith    ", 13
    //    Output: "Mr%20John%20Smith"
    // time complexity: O(n) , space complexity: O(n)
    public static String URLify(String s, int n) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++) {
            char c = s.charAt(i);
            sb.append(c == ' ' ? "%20" : String.valueOf(s.charAt(i)));
        }
        return sb.toString();
    }


    // solution 1.4: return true if any of the permutation of the string is palindrome
    // my solution is better because i can handle all characters other than lowercase alphabits
    public static Boolean isPermutationPalindrome(String s) { // very bad naming
        int vector = 0;
        int index;
        for (char e : s.toCharArray()) {
            index = getCharIndex(e);
            if (index < 0) continue;
            vector = flibBit(index, vector);
        }
        return vector == 0 || hasOnlyOneSet(vector);
    }

    private static int getCharIndex(char e) {
        char le = Character.toLowerCase(e);
        if ('a' <= le && le <= 'z') {
            return le - 'a';
        }
        return -1;
    }

    private static int flibBit(int index, int vector) {
        if (index < 0 || index > 25) return vector;
        int fb = 1;
        fb = fb << index;
        return vector ^ fb;
    }

    private static boolean hasOnlyOneSet(int vector) {
        return (vector & (vector - 1)) == 0;
    }


    // solution 1.6: given a string -> compress it
    // input: aabcccccaaa
    // output:a2b1c5a3
    //        a2b1c5a3
    private static String compressString(String s) {
        int n = s.length();
        if (n == 0) return s;
        char lastChar = s.charAt(0);
        int c = 1;
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i < n; i++) {
            if (lastChar == s.charAt(i)) c++;
            else {
                sb.append(lastChar).append(c);
                lastChar = s.charAt(i);
                c = 1;
            }
        }
        // append last char with its count
        sb.append(lastChar).append(c);
        return sb.length() < n ? sb.toString() : s;
    }

    // todo: solution 1.7: rotate array in place ( i get it but having trouble implementing it myself whiteboard 152)

    // solution 1.8: for each zero element in a 2d array set its column and row into zero
    public static void setZeroArray(int[][] matrix) {
        int n = matrix.length;
        int m = matrix[0].length;
        // check if the first row or column has zero
        boolean hasFRZ = false, hasFCZ = false;
        for (int e : matrix[0]) {
            if (e == 0) {
                hasFRZ = true;
                break;
            }
        }
        for (int i = 0; i < m; i++) {
            if (matrix[i][0] == 0) {
                hasFCZ = true;
                break;
            }
        }

        // set first row and column to zero -> for in-place solution
        for (int i = 1; i < n; i++) {
            for (int j = 1; j < m; j++) {
                if (matrix[i][j] == 0) {
                    matrix[0][j] = 0;
                    matrix[i][0] = 0;
                }
            }
        }
        System.out.println(Arrays.deepToString(matrix));

        // nullify rows and columns
        for (int i = 0; i < n; i++) {
            if (matrix[i][0] == 0) nullifyRow(i, matrix);
        }
        System.out.println(Arrays.deepToString(matrix));

        for (int j = 0; j < m; j++) {
            if (matrix[0][j] == 0) nullifyColumn(j, matrix);
        }

        if (hasFRZ) nullifyRow(0, matrix);
        if (hasFCZ) nullifyColumn(0, matrix);
    }

    public static void nullifyRow(int r, int[][] matrix) {
        for (int j = 0; j < matrix[0].length; j++) {
            matrix[r][j] = 0;
        }
    }

    public static void nullifyColumn(int c, int[][] matrix) {
        for (int i = 0; i < matrix.length; i++) {
            matrix[i][c] = 0;
        }
    }

    // 1.9 given 2 strings s1 , s2 -> check if s2 is a rotation of s1
    public static boolean isStringRotation(String s1, String s2) {
        if (s1.length() != s2.length()) return false;
        String s1s1 = s1 + s1;
        return s1s1.contains(s1);
    }


    public static void main(String[] args) {
        System.out.println(hasUniqueChar("abcdc"));
        System.out.println(isPermutation("abcdo", "bdaao"));
        System.out.println(URLify("Mr John Smith    ", 13));
        System.out.println(isPermutationPalindrome("Tact Coa"));
        System.out.println(compressString("aabcccccaaa"));
        int[][] matrix = {
                {1, 0, 3},
                {0, 0, 6},
                {7, 8, 9}
        };
        setZeroArray(matrix);
        System.out.println(Arrays.deepToString(matrix));
        System.out.println(isStringRotation("waterbottle", "erbottlewat"));
    }


}
