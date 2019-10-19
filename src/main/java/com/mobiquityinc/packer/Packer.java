package com.mobiquityinc.packer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import com.mobiquityinc.exception.APIException;

public class Packer {
	
	private Packer() {
	}

	public static String pack(String filePath) throws APIException {
		List<Package> packageList = extractPackagesFromFile(filePath); // Extract the packages from file
		StringBuilder sb = new StringBuilder(); // Holds the result of the pack processing
		
		// Loop through packages and get the best set for each one
		packageList.stream()
			.forEach(p -> {
				sb.append(p.getBestSet() + "\n");
			});
				
		return sb.toString();
	}
	
	/**
	 * This method receive a file with the input nedded to mount packages. Each line of the file should
	 * represent a Package with its max weight and its items. Eg: 34 : (1,23.45,€45.0) (2,45.5,€89) 
	 * @param filePath - The absolute path of the input file
	 * @return A list of Package with it items
	 * @throws APIException
	 */
	public static List<Package> extractPackagesFromFile(String filePath) throws APIException {
	    try (Stream<String> str = Files.lines(Paths.get(filePath))) {
	        return Packer.extractPackagesFromStream(str);
	    } catch(IOException ioEx) {
	        throw new APIException(
                    String.format("An exception occurred reading file: '%s'",
                    filePath), ioEx);
	    }
	}
	
	/**
	 * Parses the package information from a Stream of Strings. It will consider each line separeted by a line break as 
	 * a Package information so it can parse it and mount the package.
	 * @param str
	 * @return
	 * @throws APIException
	 */
	public static List<Package> extractPackagesFromStream(Stream<String> str) throws APIException {
		final List<Package> packageList = new ArrayList<>();		
		
		// Loop through the lines (\n) inside the str
		str.forEach(apiExceptionThrower(l -> {
		    // Check if the current line l is in the expected format.
		    if (!isLineSyntaxOk(l)) {
		        throw new APIException(String.format("The syntax of line '%s' is incorrect.", l));
		    }
		    
			final String[] weightAndItems = l.split(":"); // Split the line by the colon so we can separete the package weight and items
			final String[] items = weightAndItems[1].trim().split(" "); // Split the right side of the above split by space to get the individual items
			final Package pkg = new Package(Double.parseDouble(weightAndItems[0])); // Start to mount the Package

			// Insert items into the package
			Arrays.stream(items)
				.forEach(apiExceptionThrower((String item) -> {
				    // Remove (, ) and € characteres and split by ,
					String[] itemParts = 
							item.replace("(", "")
								.replace(")", "")
								.replace("€", "")
								.trim().split(",");
					// Mount the Package Item
					pkg.addItem(new PackageItem(Integer.parseInt(itemParts[0]),
												Double.parseDouble(itemParts[1]),
												Double.parseDouble(itemParts[2])));
				}));			
			
			packageList.add(pkg);
		}));		
		
		return packageList;
	}
	
	/**
	 * This method is responsible to check if a line has the correct syntax
	 * @param line A string representing a line from a file
	 * @return true if the line has the correct syntax, false otherwise.
	 */
	public static boolean isLineSyntaxOk(String line) {
	    return Pattern
	            .matches("\\d+ ?\\: ?(\\(\\d+\\,\\d+\\.?\\d{0,2}\\,\\€?\\d+\\.?\\d{0,2}\\) )*(\\(\\d+\\,\\d+\\.?\\d{0,2}\\,\\€?\\d+\\.?\\d{0,2}\\)) ?\\n?", 
	                    line);
	}
	
	/**
	 * Method to be called on lambda to throw an Checked Exception
	 * @author Lucas Henrique Vicente <lucashv@gmail.com>
	 */
	private static <T> Consumer<T> apiExceptionThrower(Readable<T, APIException> obj) throws APIException {
		return i -> {
			try {
				obj.accept(i);
			} catch (APIException e) {
				throw new RuntimeException(e);
			}
		};
	}
	
	/**
	 * Wrapper interface so we can throw a Checked Exception from inside a lambda 
	 * @author lucas
	 */
	@FunctionalInterface
	private interface Readable<T, E extends Exception> {
		void accept(T l) throws E;
	}
}
