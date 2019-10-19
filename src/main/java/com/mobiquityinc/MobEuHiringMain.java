package com.mobiquityinc;

import org.apache.log4j.Logger;

import com.mobiquityinc.exception.APIException;
import com.mobiquityinc.packer.Packer;

public class MobEuHiringMain {
	private static final Logger LOGGER = Logger.getLogger(MobEuHiringMain.class);
	
	public static void main(String... args) {
		try {
			if (args.length == 0) {
				LOGGER.info("A file path is nedded! Exiting.");
				return;
			}
			System.out.println(Packer.pack(args[0]));
		} catch(APIException apiEx) {
			LOGGER.error("Something went wrong!", apiEx);
		}
	}
}
