package com.gbs.gbsproject.service;

import com.gbs.gbsproject.dao.CertificateDao;
import com.gbs.gbsproject.model.Certificate;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.IOException;

public class CertificateService {
    private static final Logger LOGGER = Logger.getLogger(CertificateService.class.getName());

    public String generateCertificate(Certificate certificate) {
        try {
            String fullName = certificate.fullName();
            String currentDate = certificate.issueDate()
                    .format(java.time.format.DateTimeFormatter.ofPattern("MMMM d, yyyy"));

            String userHome = System.getProperty("user.home");
            String downloadsPath = userHome + File.separator + "Downloads";
            String pdfPath = downloadsPath + File.separator + "Certificate_of_Completion.pdf";

            Document document = new Document(PageSize.A4.rotate());
            PdfWriter.getInstance(document, new FileOutputStream(pdfPath));
            document.open();

            // Optional: adjust if using resource loading instead of hardcoded path
            String backgroundPath = Objects.requireNonNull(getClass().getClassLoader().getResource("background.jpg")).getPath();
            Image backgroundImage = Image.getInstance(backgroundPath);
            backgroundImage.setAbsolutePosition(0, 90);
            backgroundImage.scaleToFit(PageSize.A4.getHeight(), PageSize.A4.getWidth());
            document.add(backgroundImage);

            Font titleFont = new Font(Font.FontFamily.HELVETICA, 48, Font.BOLD, BaseColor.BLACK);
            Paragraph title = new Paragraph("Congratulations!", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingBefore(100);
            document.add(title);

            Font bodyFont = new Font(Font.FontFamily.HELVETICA, 24, Font.NORMAL, BaseColor.BLACK);
            Paragraph body = new Paragraph("This certificate is awarded to", bodyFont);
            body.setAlignment(Element.ALIGN_CENTER);
            body.setSpacingAfter(20);
            document.add(body);

            Font nameFont = new Font(Font.FontFamily.HELVETICA, 30, Font.BOLDITALIC, BaseColor.BLACK);
            Paragraph recipient = new Paragraph(fullName, nameFont);
            recipient.setAlignment(Element.ALIGN_CENTER);
            recipient.setSpacingAfter(40);
            document.add(recipient);

            Paragraph details = new Paragraph("For successfully completing the \n GBS Program", bodyFont);
            details.setAlignment(Element.ALIGN_CENTER);
            details.setSpacingAfter(40);
            document.add(details);

            Paragraph line = new Paragraph("------------------------------------------------------------");
            line.setAlignment(Element.ALIGN_CENTER);
            line.setSpacingAfter(40);
            document.add(line);

            Paragraph date = new Paragraph("Date: " + currentDate, bodyFont);
            date.setAlignment(Element.ALIGN_CENTER);
            document.add(date);

            document.close();

            CertificateDao dao = new CertificateDao();
            int certId = dao.saveCertificate(certificate);
            if (certId != -1) {
                System.out.println("Certificate record saved with ID: " + certId);
            } else {
                LOGGER.warning("Certificate was generated, but saving to database failed.");
            }


            return pdfPath;

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to generate certificate", e);
            return null;
        }
    }


    public void openCertificate(String pdfPath) {
        if (pdfPath == null) return;

        String os = System.getProperty("os.name").toLowerCase();
        ProcessBuilder processBuilder;

        try {
            if (os.contains("win")) {
                processBuilder = new ProcessBuilder("cmd", "/c", "start", "chrome", pdfPath);
            } else if (os.contains("mac")) {
                processBuilder = new ProcessBuilder("open", pdfPath);
            } else if (os.contains("nix") || os.contains("nux")) {
                processBuilder = new ProcessBuilder("xdg-open", pdfPath);
            } else {
                LOGGER.warning("Unsupported OS: " + os);
                return;
            }

            Process process = processBuilder.start();
            process.waitFor();

        } catch (IOException | InterruptedException e) {
            LOGGER.log(Level.SEVERE, "Failed to open PDF", e);
        }
    }
}
