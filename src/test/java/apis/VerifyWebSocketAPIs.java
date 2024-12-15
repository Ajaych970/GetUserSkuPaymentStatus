package apis;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.java_websocket.client.WebSocketClient;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class VerifyWebSocketAPIs {
    SocketServiceData context;
    WebClient webClient;


    @BeforeTest
    public void createContext() throws IOException {
        FileInputStream readUrl=new FileInputStream("src/main/resources/baseUrl.properties");
        Properties p=new Properties();
        p.load(readUrl);
        context=new SocketServiceData();
        context.URI = p.getProperty("baseUrl");
        context.requestHeaders=getHeader();
        context.timeOut=10;
        context.expectedMessage="This is a test";
        context.actualMessage="This is a test";
    }

    @Test
    public void verifyWebSocketAPI(){
        SocketServiceData responseContext=WebClient.getInstance().connectAndListen(context);
        try {
            webClient.sentMesssage();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

//        Assert.assertEquals(responseContext.statusCode,200,"Status code is different");
    }

//    @Test
//    public void verifyWebSocketAPI_TimeOutsAutomatically(){
//        context.actualMessage="Invalid message";
//        SocketServiceData responseContext=WebClient.getInstance().connectAndListen(context);
//
//        Assert.assertEquals(responseContext.statusCode,1006,"Status code is different");
//    }





    public static Properties readHeaderFile() throws FileNotFoundException {
        FileInputStream header=new FileInputStream("src/main/resources/Header.properties");
        Properties properties=new Properties();
        try {
            properties.load(header);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return properties;
    }



    public static Map<String,String> getHeader() throws FileNotFoundException {
        Map<String,String> header=new HashMap<>();

        for (String key : readHeaderFile().stringPropertyNames()) {
            header.put(key, readHeaderFile().getProperty(key));
        }

        return header;
    }
}
