package com.billflow;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class Emphasis {

	public static void main(String[] args) {
		
		
		int[] numArray = {2,1,5,3,4};
		
		for (int i = 0; i < numArray.length; i++) {
			
			for(int j = i; j < numArray.length; j++) {
				if(numArray[i]<numArray[j]) {
					System.out.println(numArray[i]);
				}
			}
		}
		
	}
}
