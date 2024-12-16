package apis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URISyntaxException;
import java.util.Map;

public class WebClient {
    private Client webSocket;
    private final ObjectMapper objectMapper=new ObjectMapper();


    private WebClient() {
    }

    public static WebClient getInstance() {
        return new WebClient();
    }

    public SocketServiceData connectAndListen(SocketServiceData socketContext) throws JsonProcessingException {
        boolean isSent=false;
        try {
            webSocket = new Client(socketContext);
            System.out.println("Websocket : "+webSocket);

            if (!socketContext.requestHeaders.isEmpty()) {
                final Map<String, String> requestHeaderParams = socketContext.requestHeaders;
                requestHeaderParams.forEach((key, value) -> {
                    webSocket.addHeader(key, value);
                });
            }
            boolean b = webSocket.connectBlocking();
            System.out.println("Webscocket connection blocking : "+b);
            System.out.println("Webscocket isClosed : "+webSocket.isClosed());

            while (!webSocket.isClosed()){
                if(webSocket.connectionAliveTime()>=socketContext.timeOut){
                    webSocket.close(1006,"Time Out");
                }
                if(!isSent){
//                    webSocket.onMessage(socketContext.actualMessage);
                    sentMesssage();
                    isSent=true;

                }
            }

        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("WebSocket Closed.....");
        return socketContext;
    }

//    public void sendMessageToWebSocket(SocketServiceData socketContext) throws JsonProcessingException, URISyntaxException {
////        webSocket = new Client(socketContext);
//        boolean isSent=false;
//        System.out.println("send message log.....");
//        while (!webSocket.isClosed()){
//            if(webSocket.connectionAliveTime()>=socketContext.timeOut){
//                webSocket.close(1006,"Time Out");
//            }
//            if(!isSent){
////                    webSocket.onMessage(socketContext.actualMessage);
//                sentMesssage();
//                isSent=true;
//
//            }
//        }
//
//    }




    public void sentMesssage() throws JsonProcessingException {
        Map<String, Object> requestData = Client.readJsonFromFile("src/main/java/Request/request.json");
        String jsonRequest = objectMapper.writeValueAsString(requestData);

        webSocket.send(jsonRequest);
        System.out.println("Sent json request :" + jsonRequest);


    }

    public void receivedMessage(){
//        webSocket.onMessage();

    }
}
