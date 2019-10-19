package com.mobiquityinc.packer;

import com.mobiquityinc.exception.APIException;

/**
 * Class to represent a Package Item. It holds the index, weight and cost of a single
 * package item.
 * @author Lucas Henrique Vicente <lucashv@gmail.com>
 */
public class PackageItem {
    private static final double MAX_ITEM_WEIGHT = 100.0;
    private static final double MAX_ITEM_COST = 100.0;
    
	private final int index;
	private final double weight;
	private final double cost;
	
	public PackageItem(int index, double weight, double cost) throws APIException {
	    // Check if exceded that max item weight
	    if (weight > MAX_ITEM_WEIGHT)
	        throw new APIException("The Weight of the item can not pass " + MAX_ITEM_WEIGHT);
	    
	    // Check if exceded the max item cost
	    if (cost > MAX_ITEM_COST)
	        throw new APIException("The Cost of the item can not pass " + MAX_ITEM_COST);
	        
		this.index = index;
		this.weight = weight;
		this.cost = cost;
	}

	public int getIndex() {
		return index;
	}

	public double getWeight() {
		return weight;
	}

	public double getCost() {
		return cost;
	}
	
	@Override
	public String toString() {
		return String.format("(%d, %.2f, %.2f)",
							 this.index,
							 this.weight,
							 this.cost);
	}
}
