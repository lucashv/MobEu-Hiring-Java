package com.mobiquityinc.packer.test;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.util.List;

import com.mobiquityinc.packer.Package;
import com.mobiquityinc.packer.Packer;

public class PackerTest {
    @Test
    public void shouldReturnValidLineSyntax() throws Exception {
        assertTrue(Packer.isLineSyntaxOk("20 : (1,10.60,€2) (2,20.0,€3) (3,30.0,€4)\n"));
    }

    @Test
    public void shouldReturnInvalidLineSyntax() throws Exception {
        assertTrue(!Packer.isLineSyntaxOk("20 : (1,10.60,€2) (2,20.0,€3) (3,30.0,€4\n"));
    }

    @Test(expected = Exception.class)
    public void shouldThrowExceptionDueBadFormedFile() throws Exception {
        String strPkgs = new StringBuilder().append("10 : (1,45.60,€5) (2,34.0,€3) (3,23.0,€15\n")
                .toString();
        Packer.extractPackagesFromStream(strPkgs.lines());
    }

    @Test
    public void shouldExtractPackagesFromStream() throws Exception {
        String strPkgs = new StringBuilder().append("10 : (1,45.60,€5) (2,34.0,€3) (3,23.0,€15)\n")
                .append("20 : (1,10.60,€2) (2,20.0,€3) (3,30.0,€4)\n")
                .append("30 : (1,100.00,€100)\n").toString();
        List<Package> packages = Packer.extractPackagesFromStream(strPkgs.lines());
        assertTrue(packages != null && packages.size() == 3
                && packages.get(0).getWeightLimit() == 10 && packages.get(1).getWeightLimit() == 20
                && packages.get(2).getWeightLimit() == 30);
    }

    @Test
    public void shouldPackItemsFromFile() throws Exception {
        String fileContent = new StringBuilder()
                .append("81 : (1,53.38,€45) (2,88.62,€98) (3,78.48,€3) (4,72.30,€76) (5,30.18,€9) (6,46.34,€48)\n")
                .append("8 : (1,15.3,€34)\n")
                .append("75 : (1,85.31,€29) (2,14.55,€74) (3,3.98,€16) (4,26.24,€55) (5,63.69,€52) (6,76.25,€75) (7,60.02,€74) (8,93.18,€35) (9,89.95,€78)\n")
                .append("56 : (1,90.72,€13) (2,33.80,€40) (3,43.15,€10) (4,37.97,€16) (5,46.81,€36) (6,48.77,€79) (7,81.80,€45) (8,19.36,€79) (9,6.76,€64)")
                .toString();        
        File tmpFile = File.createTempFile("pack-test-file", "txt");
        Files.write(tmpFile.getAbsoluteFile().toPath(), fileContent.getBytes());
        String result = Packer.pack(tmpFile.getPath());
        assertTrue("4\n-\n2,3,4\n8,9\n".equals(result));
    }
}
