package com.osspar.comparison;

import com.draftable.api.client.Comparison;
import com.draftable.api.client.Comparisons;
import com.draftable.api.client.Export;
import com.github.romankh3.image.comparison.ImageComparison;
import com.github.romankh3.image.comparison.ImageComparisonUtil;
import com.github.romankh3.image.comparison.model.ImageComparisonResult;
import com.github.romankh3.image.comparison.model.ImageComparisonState;
import com.groupdocs.comparison.Comparer;
import com.groupdocs.comparison.common.comparisonsettings.ComparisonSettings;
import com.groupdocs.comparison.common.comparisonsettings.PdfComparisonSettings;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.time.Instant;

/**
 * Main class for showing the usage.
 */
public class Main {

    String outputPath = "src/main/resources/result.png";
    String outputPath_pdf = "src/main/resources/result";
    String resources_path = "src/main/resources/";

    String oldPdfName = "Individual_View_Old.pdf";
    String newPdfName = "Individual_View_New.pdf";

    String oldImageName = "Individual_View_Old.jpg";
    String newImageName = "Individual_View_New.jpg";

    public static void main(String[] args) {
        Main main = new Main();
        //main.compareImage();
        //main.comparePdf();
        main.drafTableCompare();
        //main.pdfBox();
    }

    public void pdfBox() {
        String oldPdfPath = resources_path + oldPdfName;
        String newPdfPath = resources_path + newPdfName;
        PDDocument pd;
        BufferedWriter wr;
        try {
            File input = new File(oldPdfPath);  // The PDF file from where you would like to extract
            File output = new File(resources_path + "SampleText.txt"); // The text file where you are going to store the extracted data
            pd = PDDocument.load(input);
            System.out.println(pd.getNumberOfPages());
            System.out.println(pd.isEncrypted());
            pd.save("CopyOfBill.pdf"); // Creates a copy called "CopyOfInvoice.pdf"
            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setStartPage(1); //Start extracting from page 3
            stripper.setEndPage(1); //Extract till page 5
            wr = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(output)));
            stripper.writeText(pd, wr);
            if (pd != null) {
                pd.close();
            }
            // I use close() to flush the stream.
            wr.close();
            System.out.println("done ---");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void drafTableCompare() {
        try {
            String oldImgPath = resources_path + oldPdfName;
            String newImgPath = resources_path + newPdfName;
            File oldFile = new File(oldImgPath);
            File newFile = new File(newImgPath);
            Comparisons comparisons = new Comparisons("dafCMh-test", "dc53d0761f4624c3c1af5d66c0252234");
            //Comparisons comparisons = new Comparisons("", "", "http://localhost:4200/");

            Comparison comparison = comparisons.createComparison(
                    Comparisons.Side.create(oldFile, "pdf", oldFile.getName()),
                    Comparisons.Side.create(newFile, "pdf", newFile.getName()), null, true, Instant.now().plusSeconds(1200)
            );
            System.out.println(String.format("Comparison created: %s", comparison));


            // Generate a signed viewer URL to access the private comparison. The expiry
            // time defaults to 30 minutes if the validUntil parameter is not provided.
            //String viewerURL = comparisons.signedViewerURL(comparison.getIdentifier());
            //System.out.println(String.format("Viewer URL (expires in 30 mins): %s", viewerURL));

            String viewerURL = comparisons.publicViewerURL(comparison.getIdentifier());
            System.out.println(String.format("Public url: %s", viewerURL));

            try (BufferedInputStream in = new BufferedInputStream(new URL(viewerURL).openStream());
                 FileOutputStream fileOutputStream = new FileOutputStream(resources_path+"abcd.pdf")) {
                byte dataBuffer[] = new byte[1024];
                int bytesRead;
                while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                    fileOutputStream.write(dataBuffer, 0, bytesRead);
                }
            } catch (IOException e) {
                // handle exception
            }

            Export export = comparisons.getExport(comparison.getIdentifier());
            System.out.println("export = " + export);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void compareImage() {
        String oldImgPath = resources_path + oldImageName;
        String newImgPath = resources_path + newImageName;
        // load the images to be compared
        //BufferedImage bufferedImage1 = ImageComparisonUtil.readImageFromResources("Individual_View_New.jpg");
        //BufferedImage bufferedImage2 = ImageComparisonUtil.readImageFromResources("Individual_View_Old.jpg");

        // where to save the result (leave null if you want to see the result in the UI)
        File resultDestination = new File(outputPath);

        //Create ImageComparison object for it.
        //ImageComparison imageComparison = new ImageComparison(bufferedImage1, bufferedImage2, resultDestination);

        //Can be used another constructor for it, without destination.
        ImageComparison imageComparison = new ImageComparison(oldImgPath, newImgPath);
        //or
        //new ImageComparison(bufferedImage1, bufferedImage2);

        //Also can be configured BEFORE comparing next properties:

        //Threshold - it's the max distance between non-equal pixels. By default it's 5.
        imageComparison.setThreshold(2);
        System.out.println("threshold -- " + imageComparison.getThreshold());

        //RectangleListWidth - Width of the line that is drawn in the rectangle. By default it's 1.
        imageComparison.setRectangleLineWidth(2);

        //Destination. Before comparing also can be added destination file for result image.
        imageComparison.setDestination(resultDestination);
        //imageComparison.getDestination();
        System.out.println("pixcel tollerance " + imageComparison.getPixelToleranceLevel());
        System.out.println("pixcel diff " + imageComparison.getAllowingPercentOfDifferentPixels());
        //imageComparison.setPixelToleranceLevel(0.1);
        imageComparison.setAllowingPercentOfDifferentPixels(0.5);
        //MaximalRectangleCount - It means that would get first x biggest rectangles for drawing.
        // by default all the rectangles would be drawn.
        //imageComparison.setMaximalRectangleCount(10);
        //imageComparison.getMaximalRectangleCount();

        //MinimalRectangleSize - The number of the minimal rectangle size. Count as (width x height).
        // by default it's 1.
        //imageComparison.setMinimalRectangleSize(100);

        //After configuring the ImageComparison object, can be executed compare() method:
        ImageComparisonResult comparisonResult = imageComparison.compareImages();

        //Can be found ComparisonState.
        ImageComparisonState comparisonState = comparisonResult.getImageComparisonState();
        System.out.println("comparisonState --- " + comparisonState.name());
        //And Result Image
        BufferedImage resultImage = comparisonResult.getResult();

        //Image can be saved after comparison, using ImageComparisonUtil.
        ImageComparisonUtil.saveImage(resultDestination, resultImage);
        System.out.println("Diff calculated and saved on " + resultDestination.getAbsolutePath());
    }


    public void comparePdf() {
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        InputStream is = classloader.getResourceAsStream(oldPdfName);
        InputStream is2 = classloader.getResourceAsStream(newPdfName);
        /*try {

            //new PdfComparator(is, is2).compare().writeTo(outputPath_pdf);
            new PdfComparator(is, is2).getResult().getDifferencesJson();
            new PdfComparator(is, is2).getResult().writeTo(outputPath_pdf);
            System.out.println("file comparision done");
        } catch (Exception ex) {
            ex.printStackTrace();
        }*/

        try {
            Comparer comparer = new Comparer();
            ComparisonSettings settings = new PdfComparisonSettings();
            settings.setShowDeletedContent(true);
            settings.setUseFramesForDelInsElements(true);
            settings.setGenerateSummaryPage(false);
            settings.setStyleChangeDetection(true);
            comparer.compare(resources_path + oldPdfName, resources_path + newPdfName, settings).saveDocument(resources_path + "res.pdf");
            System.out.println("done");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
