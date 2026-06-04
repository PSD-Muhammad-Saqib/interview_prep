package saqibdev.leetcode.Arrays;

/*
 * Base Problem:
 * 88. Merge Sorted Array
 *
 * Practice Problems:
 * 1. Remove Duplicates from Sorted Array (26)
 * 2. Move Zeroes (283)
 * 3. Merge Two Sorted Arrays (simple - return new array)
 */

public class MergeSortedArray {

    // 88. Merge Sorted Array (Top 150)
    public static void merge(int[] nums1, int m, int[] nums2, int n) {
        if (n == 0) return;
    }

    public static void testMergeSortedArray() {
        int[] nums1 = {1, 2, 3, 0, 0, 0};
        int[] nums2 = {2, 5, 6};

        merge(nums1, 3, nums2, 3);

        System.out.print("Merge Sorted Array: ");
        for (int num : nums1) {
            System.out.print(num + " ");
        }
        System.out.println();
    }

    // ----- Practice Problems -----

    // 1. Remove Duplicates from Sorted Array
    public static int removeDuplicates(int[] nums) {
        // you implement
        if (nums == null || nums.length == 0) return 0;
        if (nums.length == 1) return 1;

        int pointer1 = 0;
        int pointer2 = 1;

        while(pointer2 < nums.length) {
            if(nums[pointer1] != nums[pointer2]){

            }
            pointer2++;
            pointer1++;
        }
        return 0;
    }

    public static void testRemoveDuplicates() {
        int[] dupArr = {1, 1, 2, 2, 3};
        int k = removeDuplicates(dupArr);

        System.out.print("Remove Duplicates: ");
        for (int i = 0; i < k; i++) {
            System.out.print(dupArr[i] + " ");
        }
        System.out.println();
    }

    // 2. Move Zeroes
    public static void moveZeroes(int[] nums) {
        // you implement
    }

    public static void testMoveZeroes() {
        int[] zeroArr = {0, 1, 0, 3, 12};
        moveZeroes(zeroArr);

        System.out.print("Move Zeroes: ");
        for (int num : zeroArr) {
            System.out.print(num + " ");
        }
        System.out.println();
    }

    // 3. Merge Two Sorted Arrays (return new array)
    public static int[] mergeSimple(int[] a, int[] b) {
        // you implement
        return new int[0];
    }

    public static void testMergeSimple() {
        int[] a = {1, 3, 5};
        int[] b = {2, 4, 6};

        int[] merged = mergeSimple(a, b);

        System.out.print("Merge Simple: ");
        for (int num : merged) {
            System.out.print(num + " ");
        }
        System.out.println();
    }

    // ----- Base Function -----

    public static void main(String[] args) {
        testMergeSortedArray();
        testRemoveDuplicates();
        testMoveZeroes();
        testMergeSimple();
    }
}