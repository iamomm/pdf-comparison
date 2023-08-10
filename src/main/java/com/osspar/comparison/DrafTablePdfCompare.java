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
        DrafTablePdfCompare drafTablePdfCompare = new DrafTablePdfCompare();
        drafTablePdfCompare.drafTableCompare();
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
                    Comparisons.Side.create(oldFile, "pdf", oldPdfName),
                    Comparisons.Side.create(newFile, "pdf", newPdfName)
            );
            //, null, true, Instant.now().plusSeconds(1200)
            String comparisonId = comparison.getIdentifier();
            comparison = comparisons.getComparison(comparisonId);

            System.out.println(String.format("Comparison created: %s", comparison));

            //Generate a signed viewer URL to access the private comparison. The expiry
            //time defaults to 30 minutes if the validUntil parameter is not provided.
            //String viewerURL = comparisons.signedViewerURL(comparison.getIdentifier());
            //System.out.println(String.format("Viewer URL (expires in 30 mins): %s", viewerURL));

            //String viewerURL = comparisons.signedViewerURL(comparison.getIdentifier());
            //System.out.println(String.format("Public url: %s", viewerURL));

            /*try (BufferedInputStream in = new BufferedInputStream(new URL(viewerURL).openStream());
                 FileOutputStream fileOutputStream = new FileOutputStream(resources_path + "abcd.pdf")) {
                byte dataBuffer[] = new byte[1024];
                int bytesRead;
                while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                    fileOutputStream.write(dataBuffer, 0, bytesRead);
                }
            } catch (IOException e) {
                e.printStackTrace();
                //handle exception
            }*/

            Export export = comparisons.createExport(comparisonId, ExportKind.SINGLE_PAGE);

            System.out.println(String.format("Post export created id: %s", export.getIdentifier()));
            System.out.println(String.format("Post export created cp: %s", export.getComparison()));
            Export getExport = comparisons.getExport(export.getIdentifier());

            System.out.println(String.format("exporting data id: %s", getExport.getIdentifier()));
            System.out.println(String.format("exporting data cp: %s", getExport.getComparison()));
            System.out.println(String.format("exporting data ur: %s", getExport.getUrl()));
            System.out.println(String.format("exporting data ready: %s", getExport.isReady()));
            System.out.println(String.format("exporting data failed: %s", getExport.getFailed()));
            System.out.println(String.format("exporting data failed: %s", getExport.getErrorMessage()));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
