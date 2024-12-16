package apis;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URISyntaxException;
import java.util.Map;

public class WebClient_2 {

    private Client webSocket;
    ObjectMapper objectMapper;


    public synchronized void connectToWebSocket(SocketServiceData socketContext) throws URISyntaxException, InterruptedException {
        webSocket = new Client(socketContext);
        System.out.println("WebSocket instance created: " + webSocket);

        // Add headers if they exist
        if (!socketContext.requestHeaders.isEmpty()) {
            socketContext.requestHeaders.forEach(webSocket::addHeader);
        }

        // Establish a blocking connection
        boolean isConnected = webSocket.connectBlocking();
        if (!isConnected) {
            throw new IllegalStateException("WebSocket connection could not be established.");
        }
        System.out.println("WebSocket connection established: " + isConnected);
        System.out.println("WebSocket is closed: " + webSocket.isClosed());
    }


    public void sendMessageToWebSocket() throws JsonProcessingException {
        objectMapper=new ObjectMapper();
        if (webSocket == null || !webSocket.isOpen()) { // Check if WebSocket is connected
            throw new IllegalStateException("WebSocket is not connected. Please connect first.");
        }

        // Prepare the JSON request data
        Map<String, Object> requestData = Client.readJsonFromFile("src/main/java/Request/request.json");
        String jsonRequest = objectMapper.writeValueAsString(requestData);

        // Send the JSON request
        webSocket.send(jsonRequest);
        System.out.println("Sent JSON request: " + jsonRequest);
    }

    /**
     * Listens to the WebSocket and closes it if connection timeout occurs.
     *
     * @param socketContext SocketServiceData containing timeout details.
     */
    public void listenAndHandle(SocketServiceData socketContext) {
        if (webSocket == null) {
            throw new IllegalStateException("WebSocket is not connected. Please connect first.");
        }

        try {
            while (!webSocket.isClosed()) {
                if (webSocket.connectionAliveTime() >= socketContext.timeOut) {
                    webSocket.close(1006, "Timeout occurred.");
                    System.out.println("WebSocket closed due to timeout.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.out.println("WebSocket listener terminated.");
            System.out.println();
            System.out.println();
            System.out.println();
        }
    }


/*

//    This is rety code
    public void connectToWebSocketWithRetry(SocketServiceData socketContext, int retryCount) throws Exception {
        int attempts = 0;
        while (attempts < retryCount) {
            try {
                connectToWebSocket(socketContext);
                return; // Exit the loop if connection is successful
            } catch (IllegalStateException e) {
                attempts++;
                System.out.println("Retrying WebSocket connection... Attempt: " + attempts);
                Thread.sleep(1000); // Wait before retrying
            }
        }
        throw new IllegalStateException("Failed to connect to WebSocket after " + retryCount + " attempts.");
    }

 */





}
