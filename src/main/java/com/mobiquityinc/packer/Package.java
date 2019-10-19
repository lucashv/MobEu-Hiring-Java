package com.mobiquityinc.packer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.mobiquityinc.exception.APIException;

/**
 * Class to represent a Package. It holds the package weight and its items.
 * Also, it does calculate what is the best set of items it could have based on its items
 * and max WEIGHT.
 * @author Lucas Henrique Vicente <lucashv@gmail.com>
 */
public class Package {
	private static final double MAX_PACKAGE_WEIGHT = 100.0;
	private static final int MAX_ITEMS_IN_PACKAGE = 15;
	
	private double weightLimit;
	private List<PackageItem> items;
	
	public Package(double weightLimit) throws APIException {
		// Check if the inicial weight exceds tha package max weight
	    if (weightLimit > MAX_PACKAGE_WEIGHT)
			throw new APIException("The max wight of a package can not pass " + MAX_PACKAGE_WEIGHT);
		
		this.weightLimit = weightLimit;
		this.items = new ArrayList<>();
	}
	
	public double getWeightLimit() {
		return weightLimit;
	}
	
	public void addItem(PackageItem item) throws APIException {
	    // Check if the package already reach its max items in it
	    if (this.items.size() == MAX_ITEMS_IN_PACKAGE)
	        throw new APIException("The Package can not have more than " + MAX_ITEMS_IN_PACKAGE + " items.");
		this.items.add(item);
	}
	
	/**
	 * Check what is the best set of items to be sent in the current Package
	 * @return A String with the indexes of the choosen item to be sent
	 */
	public String getBestSet() {
	    // First, we remove all the items that exceds the max weight of the package since they are 
	    // not going to be part of it anyways
	    List<PackageItem> filteredList = 
	            this.items.stream()
	                .filter(i -> i.getWeight() <= this.getWeightLimit())
	                .collect(Collectors.toList());
	    
	    // If the list is empty, means that it had just items that exceded the MAX WEIGHT
	    if (filteredList != null && filteredList.size() == 0)
	        return "-";
	    
	    // Sort the list by Cost Desc to make sure that we have the item with max cost as first
	    filteredList = filteredList.stream()
	        .sorted((pi1, pi2) -> Double.compare(pi2.getCost(), pi1.getCost()))       
	        .collect(Collectors.toList());
	    
	    // If the list have items with same cost, we wanna keep the lowest weight 
	    // from both of them and discard the others
	    pickLowestWeightFromDuplicateCosts(filteredList);
	    
	    // Pick up the items that will be put in the package assembling the best set
	    List<PackageItem> choosenItems = new ArrayList<>();
	    filteredList.stream().forEach(i -> {
	        double weightTillNow = 
	                choosenItems.stream()
	                    .mapToDouble(PackageItem::getWeight)
	                    .sum();
	        if ((weightTillNow + i.getWeight()) <= this.getWeightLimit())
	            choosenItems.add(i);
	    });
	    
	    // Create the string to be returned with the choosen items indexes
		return Optional.ofNullable(choosenItems)
		        .orElse(Collections.emptyList())
		        .stream()
		        .map(PackageItem::getIndex)
		        .map(String::valueOf)
		        .sorted()
		        .collect(Collectors.joining(","));
	}
	
	@Override
	public String toString() {
		String stringItems = 
				this.items.stream()
					.map(i -> i.toString())
					.collect(Collectors.joining(""));			
		
		return String.format("%.2f : %s", weightLimit, stringItems);
	}
	
	/**
	 * Method to search and keep the item with the lowest cost from items with the same cost
	 * @param filteredList A list keeping the lowest weigth items from those which had the same cost
	 */
	private void pickLowestWeightFromDuplicateCosts(List<PackageItem> filteredList) {
	    // Get all distinct costs
        List<Double> distinctCosts =
                filteredList.stream()
                    .map(p -> p.getCost())
                    .distinct()
                    .collect(Collectors.toList());
        
        final List<PackageItem> auxFilteredList = new ArrayList<>(filteredList);
        final List<PackageItem> itemsToRemove = new ArrayList<>();
        distinctCosts.stream().forEach(c -> {
            List<PackageItem> sortedItems = 
                    auxFilteredList.stream()
                        .filter(p -> p.getCost() == c)
                        .sorted(Comparator.comparingDouble(PackageItem::getWeight))
                        .collect(Collectors.toList());
            sortedItems.remove(0);
            
            itemsToRemove.addAll(sortedItems);
        });
        
        filteredList.removeAll(itemsToRemove);
	}
	
}
