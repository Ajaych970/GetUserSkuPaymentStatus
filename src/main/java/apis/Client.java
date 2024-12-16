package apis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.Map;
import java.util.Objects;

public class Client extends WebSocketClient {
    SocketServiceData dataContext;
    Date openedTime=new Date();
    Date closedTime=new Date();



    private final ObjectMapper objectMapper=new ObjectMapper();


    public Client(URI serverUri) {
        super(serverUri);
    }

    public Client(URI serverUri, Map<String, String> httpHeaders) {
        super(serverUri, httpHeaders);
    }

    public Client(SocketServiceData dataContext) throws URISyntaxException {
        super(new URI(dataContext.URI));
        this.dataContext=dataContext;
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
//        openedTime=new Date();
        System.out.println("Opened Connection "+dataContext.URI);
    }

    @Override
    public void onMessage(String message) {
        String receivedJsonData=null;
        try {
            Object jsonData = objectMapper.readValue(message, Object.class);
            receivedJsonData = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonData);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Received "+message);
//        System.out.println("Received JsonData : "+receivedJsonData);
//        dataContext.messageList.add(message);
        close();

//        if(dataContext.expectedMessage.equals(message)){
//            closeConnection(1000,"Received expected message");
//        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        int timeElapsed = connectionAliveTime();
        System.out.println("Connection time (seconds) "+timeElapsed);
        dataContext.statusCode=code;
    }

    @Override
    public void onError(Exception ex) {
        System.out.println("Error in connection "+ex.getMessage());
    }

    public int connectionAliveTime(){
//        closedTime=new Date();
        int timeInSeconds=(int) (closedTime.getTime()-openedTime.getTime())/1000;
        dataContext.timeTaken=timeInSeconds;
        return timeInSeconds;
    }

    // Method to send JSON request
    public void sendJsonRequest(Object requestData) {
        try {
            // Serialize the request object to JSON
            String jsonRequest = objectMapper.writeValueAsString(requestData);
            send(jsonRequest);
            System.out.println("Sent JSON Request: " + jsonRequest);
        } catch (JsonProcessingException e) {
            System.err.println("Failed to serialize request data to JSON: " + e.getMessage());
        }
    }


    // Method to read JSON from input file
    public static Map<String, Object> readJsonFromFile(String filePath) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            // Read the JSON from the file and convert it into a Map
            return objectMapper.readValue(new File(filePath), Map.class);
        } catch (IOException e) {
            System.err.println("Failed to read JSON from file: " + e.getMessage());
            return null;
        }
    }


    @Override
    public void close() {
        super.close();
    }
}
