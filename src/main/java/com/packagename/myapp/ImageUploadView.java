package com.packagename.myapp;

import com.google.gson.Gson;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.router.Route;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

/**
 * A Vaadin view class for the upload page
 *
 * A new instance of this class is created for every new user and every
 * browser tab/window.
 */
@Route
@CssImport("./styles/shared-styles.css")
@CssImport(value = "./styles/vaadin-text-field-styles.css", themeFor = "vaadin-text-field")
public class ImageUploadView extends VerticalLayout {

    /**
     * Construct a Vaadin view for the upload page.
     * <p>
     * Build the initial UI state for the user uploading an image.
     *
     * @param service The service for image uploading.
     */
    public ImageUploadView(@Autowired DiscoveryService service, @Autowired S3Service s3Service, @Autowired IBMClothesClassifier classifier) {
        // Add basic upload button.
        MemoryBuffer buffer = new MemoryBuffer();
        Upload upload = new Upload(buffer);
        upload.setAcceptedFileTypes("image/jpeg","image/png");
        upload.setMaxFileSize(8388608);
        upload.setMaxFiles(3);

        // On a file successfully uploading, show the output.
        upload.addSucceededListener(event -> {
            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                IOUtils.copy(buffer.getInputStream(), baos);
                byte[] bytes = baos.toByteArray();

                s3Service.uploadImage(new ByteArrayInputStream(bytes),event.getFileName());
                Map<String,String> visualRecognitionFeatures = classifier.getClothingAttributes(new ByteArrayInputStream(bytes));
                System.out.print(visualRecognitionFeatures.get("ClothModel")+" , "+visualRecognitionFeatures.get("ColorModel"));
                visualRecognitionFeatures.put("fileName",event.getFileName());
                String json = new Gson().toJson(visualRecognitionFeatures);
                System.out.println(json);
                service.addClothing(json);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        // Add the upload button and import the css class to center it.
        Div mainStuff = new Div();
        mainStuff.addClassName("centered-content");
        mainStuff.add(upload);

        add(new TopBar(), mainStuff);
    }
}
