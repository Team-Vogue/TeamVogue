package com.packagename.myapp;

import com.ibm.cloud.sdk.core.http.HttpMediaType;
import com.ibm.cloud.sdk.core.security.IamAuthenticator;
import com.ibm.watson.discovery.v1.Discovery;
import com.ibm.watson.discovery.v1.model.*;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
public class DiscoveryService {

    String key = "quleCLx3QpXA48No0xDwqlXEasla_kk2sLwRMt60Bitj";
    String url = "https://api.au-syd.discovery.watson.cloud.ibm.com/instances/f5002da5-7966-47cc-a3e9-c87dd9d590ff";

    String clothesCollection = "f2ee7a33-3dcd-42bc-81d5-431738ff9173";
    String clothesEnvironment = "100a78cb-2b22-4b88-922b-97b7a63b5a1d";

    public List<String> getImageUrls(String clothesParam, String colorParam) {
        List<String> imageUrls = new ArrayList<String>();

        IamAuthenticator authenticator = new IamAuthenticator(key);
        Discovery discovery = new Discovery("2019-04-30",authenticator);
        discovery.setServiceUrl(url);

        String filter = "";
        if(!clothesParam.equalsIgnoreCase("")){
            filter += "ClothModel: "+clothesParam+", ";
        }
        if(!colorParam.equalsIgnoreCase("")){
            filter += "ColorModel: "+colorParam;
        }

        QueryOptions queryOptions = new QueryOptions.Builder(clothesEnvironment,clothesCollection)
                .query("").filter(filter).build();
        QueryResponse response = discovery.query(queryOptions).execute().getResult();

        List<QueryResult> results = response.getResults();
        for(QueryResult doc : results){
            imageUrls.add(doc.get("fileName").toString());
        }

        return imageUrls;
    }

    public void addClothing(String json) {
        IamAuthenticator authenticator = new IamAuthenticator(key);
        Discovery discovery = new Discovery("2019-04-30",authenticator);
        discovery.setServiceUrl(url);

        InputStream documentStream = new ByteArrayInputStream(json.getBytes());
        AddDocumentOptions addDocumentOptions = new AddDocumentOptions.Builder(clothesEnvironment,clothesCollection)
                .file(documentStream)
                .filename(System.nanoTime()+"")
                .fileContentType(HttpMediaType.APPLICATION_JSON)
                .build();
        DocumentAccepted accepted = discovery.addDocument(addDocumentOptions).execute().getResult();
    }

}
