package com.osspar.comparison;

import com.draftable.api.client.Comparison;
import com.draftable.api.client.Comparisons;
import com.draftable.api.client.Export;
import com.draftable.api.client.ExportKind;

import java.io.File;

/**
 * Main class for showing the usage.
 */
public class DrafTablePdfCompare {

    String outputPath = "src/main/resources/result.png";
    String outputPath_pdf = "src/main/resources/result";
    String resources_path = "src/main/resources/";

    String oldPdfName = "Individual_View_Old.pdf";
    String newPdfName = "Individual_View_New.pdf";

    String oldImageName = "Individual_View_Old.jpg";
    String newImageName = "Individual_View_New.jpg";

    public static void main(String[] args) {
        //System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.http.client.protocol.ResponseProcessCookies", "fatal");
        //System.setProperty("draftable.allowSelfSignedCerts", "1");
        DrafTablePdfCompare drafTablePdfCompare = new DrafTablePdfCompare();
        drafTablePdfCompare.drafTableCompare();
    }

    public void drafTableCompare() {
        Comparisons comparisons = null;

        try {
            String oldImgPath = resources_path + oldPdfName;
            String newImgPath = resources_path + newPdfName;
            File oldFile = new File(oldImgPath);
            File newFile = new File(newImgPath);

            comparisons = new Comparisons("dafCMh-test", "dc53d0761f4624c3c1af5d66c0252234");

            //String identifier = Comparisons.generateIdentifier();
            Comparison comparison = comparisons.createComparison(
                    Comparisons.Side.create(oldFile, "pdf", oldPdfName),
                    Comparisons.Side.create(newFile, "pdf", newPdfName)
            );
            //Comparison cp = comparison.get();
            //String comparisonId = comparison.getIdentifier();

            //Generate a signed viewer URL to access the private comparison. The expiry
            //time defaults to 30 minutes if the validUntil parameter is not provided.
            String viewerURL = comparisons.signedViewerURL(comparison.getIdentifier());
            System.out.println(String.format("Viewer URL (expires in 30 mins): %s", viewerURL));
            Thread.sleep(3000);

            Export export = comparisons.createExport(comparison.getIdentifier(), ExportKind.COMBINED, false);

            System.out.println(String.format("Post export created id: %s", export.getIdentifier()));
            System.out.println(String.format("Post export created cp: %s", export.getComparison()));
            System.out.println("Post export created ready --"+ export.isReady());
            Thread.sleep(2000);

            Export getExport = comparisons.getExport(export.getIdentifier());

            System.out.println(String.format("exporting data id: %s", getExport.getIdentifier()));
            System.out.println(String.format("exporting data cp: %s", getExport.getComparison()));
            System.out.println(String.format("exporting data ur: %s", getExport.getUrl()));
            System.out.println("exporting data ready: "+ getExport.isReady());
            System.out.println(String.format("exporting data failed: %s", getExport.getFailed()));
            System.out.println(String.format("exporting data failed: %s", getExport.getErrorMessage()));
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                comparisons.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
