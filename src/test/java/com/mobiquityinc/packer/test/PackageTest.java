package com.mobiquityinc.packer.test;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.util.stream.IntStream;

import com.mobiquityinc.exception.APIException;
import com.mobiquityinc.packer.Package;
import com.mobiquityinc.packer.PackageItem;

public class PackageTest {
    @Test
    public void shouldMountAPackageItem() throws Exception {
        PackageItem pkgItem = new PackageItem(0, 0.0, 0.0);        
        assertTrue(pkgItem != null);
    }
    
    @Test
    public void shouldMountAPackage() throws Exception {
        Package pkg = new Package(0.0);        
        assertTrue(pkg != null);
    }
    
    @Test(expected = APIException.class)
    public void shouldThrowAPIExceptionDueMaxWeightOnPackageItem() throws Exception {
        new PackageItem(1, 101.0, 1);
    }
    
    @Test(expected = APIException.class)
    public void shouldThrowAPIExceptionDueMaxCostOnPackageItem() throws Exception {
        new PackageItem(1, 1, 101);
    }
    
    @Test(expected = APIException.class)
    public void shouldThrowAPIExceptionDuePast15ItemsOnPackage() throws Exception {        
        Package pkg = new Package(0.0);

        for (int i = 0; i < 16; i++)
            pkg.addItem(new PackageItem(i, 0.0, 0.0));      
    }
    
    @Test
    public void shouldReturnBestSetForAPackage() throws Exception {
        Package pkg = new Package(10.0);
        pkg.addItem(new PackageItem(1, 10, 1));
        pkg.addItem(new PackageItem(2, 5, 5));
        pkg.addItem(new PackageItem(3, 3, 5));
        pkg.addItem(new PackageItem(4, 13, 5));
        pkg.addItem(new PackageItem(5, 4, 7));
        String retIndexes = pkg.getBestSet();
        assertTrue("3,5".equals(retIndexes));
    }
}
