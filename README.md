# TCP Server-Client Simulation with Thread Pool and Queue Mechanism

## Overview

This project simulates a TCP server-client system where the server uses a thread pool to handle multiple client connections. The server manages the waiting clients using a queue mechanism, and when the maximum concurrent connections are reached, clients exceeding the maximum wait time are discarded using a timeout mechanism.

### Features:

- **TCP Server**: Handles multiple client connections, supporting up to 2 concurrent clients.
- **Thread Pool**: The server uses a `ThreadPoolExecutor` to manage client handler threads.
- **Queue Management**: When the maximum number of connections is reached, new clients are placed in the waiting queue until a thread becomes available.
- **Timeout Handling**: If a client in the waiting queue exceeds the specified maximum wait time (6 seconds), the connection is discarded.

---

## Project Structure

- `TcpServer.java`: Server-side code responsible for handling client connections, thread pool management, and waiting queue operations.
- `TcpClient1.java`: Client 1 code that connects to the server, sends a message, and receives a response.
- `TcpClient2.java`: Client 2 code that connects to the server, sends a message, and receives a response.
- `TcpClient3.java`: Client 3 code that connects to the server, sends a message, and receives a response.

---

## TcpServer Detailed Explanation

### Features

- **Thread Pool**: The server uses `ThreadPoolExecutor` to handle a maximum of 2 concurrent clients.
    - `MAX_CLIENTS`: The maximum number of concurrent clients the server can handle (set to 2).
    - `WAIT_TIME`: The maximum time (in seconds) a client can wait in the queue before being discarded.

- **Client Handling**: Each client is handled by a separate thread (`ClientHandler`), which processes the client's request and sends a response.

- **Waiting Queue**: When the server reaches the maximum number of connections, new clients are placed in the waiting queue.
    - The server periodically checks the queue for clients and begins processing clients in the queue when a thread becomes available.
    - If a client exceeds the `WAIT_TIME` limit, the connection is discarded.

### Workflow

1. **Server Initialization**:
    - The server listens on port `8888` and waits for client connections.
    - If the current active connection count is below `MAX_CLIENTS`, the server processes the new client connection immediately.
    - If the active connection count is full, new clients are placed in the waiting queue.

2. **Client Processing**:
    - The server communicates with the client, reading the message sent by the client and sending an appropriate response.
    - After processing the request, the server sends an `END` message to indicate the end of communication.

3. **Waiting Mechanism**:
    - When the server cannot immediately process a client, the client is placed in the waiting queue.
    - The server checks the queue every 100 milliseconds to see if any thread is available to handle the client.
    - If the client waits longer than the `WAIT_TIME`, the server discards the connection.

4. **Timeout Handling**:
    - If the client's wait time in the queue exceeds the `WAIT_TIME`, the connection is discarded, and a timeout message is printed to the console.

### When the Server Thread Pool is Full (2 Clients Connected):

- **Client 3 Processing**:
    - When the server's thread pool is already handling 2 clients, Client 3 is placed in the waiting queue until a thread becomes available.
    - If Client 3 waits in the queue for more than the `WAIT_TIME` (6 seconds), it will be discarded, and a timeout message will be printed.
    - If a thread becomes available during the wait, the server will start processing Client 3 from the queue.

---

## TcpClient1, TcpClient2, TcpClient3 Detailed Explanation

### Features

- **Connection Timeout**: Clients set a 3-second timeout when trying to connect to the server.
- **Message Sending**: Clients send a fixed message to the server.
- **Response Listening**: Clients continuously listen for the server's response until receiving the `END` message.

### Workflow

1. **Connecting to the Server**:
    - The client connects to the server at the default address `localhost:8888`.

2. **Sending the Message**:
    - The client sends a message such as `"I am Client 1"` (or `"I am Client 2"`, `"I am Client 3"`).

3. **Receiving Server Response**:
    - The client listens for the server's response. Upon receiving the `END` message, the client will exit.

4. **Closing the Connection**:
    - Once communication is finished, the client will close the connection with the server.

---

## How to Run

1. **Compile Java Files**:

    - Make sure `TcpServer.java`, `TcpClient1.java`, `TcpClient2.java`, and `TcpClient3.java` are in the same directory.

    - Open a terminal and navigate to the directory containing the files.

    - Compile the Java files:

      ```bash
      javac TcpServer.java
      javac TcpClient1.java
      javac TcpClient2.java
      javac TcpClient3.java
      ```

2. **Start the Server**:

    - Run the following command in the terminal to start the server:

      ```bash
      java TcpServer
      ```

    - The server will begin listening on port `8888`, waiting for client connections.

3. **Start the Clients**:

    - In another terminal window, run the following commands for the clients:

      ```bash
      java TcpClient1
      java TcpClient2
      java TcpClient3
      ```

    - The clients will connect to the server and send messages.

---

## Configuration

- **Port**: The server listens on port `8888`.
- **Max Client Count**: The server can handle up to 2 concurrent client connections.
- **Wait Time**: The maximum time a client can wait in the queue is 6 seconds. If this time is exceeded, the client is discarded.

---

## Example Output

### Server Side:

```plaintext
=========== This is a MySQL server simulation ===========
Server started, waiting for client connections...
Current connections are full, client entering waiting queue...
Waiting for an available processing slot...
Waiting for an available processing slot...
Waiting for an available processing slot...
Waiting for an available processing slot...
```

### Client Side:

```plaintext
Connected to the server...
Message sent: I am Client 1
Server response: The server has received your message.
Processing client request...
Server response: The server has received your message: I am Client 1, processing complete.
Received the end communication flag, client will exit.
```

### Timeout Situation (Client 3):

If Client 3 waits for more than 6 seconds, the following message will appear:

```plaintext
Current connections are full, client entering waiting queue...
Waiting for an available processing slot...
Waiting for an available processing slot...
Connection request timeout, client connection discarded: /127.0.0.1
```

### Client 3 Successfully Processed:

If Client 3 is processed successfully during the wait, the following output will appear:

```plaintext
Current connections are full, client entering waiting queue...
Waiting for an available processing slot...
Waiting for an available processing slot...
Processing client request...
Server response: The server has received your message: I am Client 3, processing complete.
Received the end communication flag, client will exit.
```

---

## Notes

- The server uses `LinkedBlockingQueue` to manage the waiting queue and client connections.
- The server's `ThreadPoolExecutor` ensures that no more than `MAX_CLIENTS` clients are processed concurrently. The queue mechanism helps to manage client connections in an orderly manner.
- The connection timeout mechanism ensures that clients are discarded if they wait too long, preventing resource waste.

---

## What Happens Without a Thread Pool?

If there is no thread pool, the server needs to create a separate thread for each client connection. This may lead to the following problems:

### 1. **Excessive Thread Overhead**

- Every time a new client connects, a new thread is created by the server.
- Creating and destroying threads incurs costs, including memory allocation and CPU time consumption.
- If the number of client connections is large, this may result in the creation of too many threads, increasing the system's resource overhead.

### 2. **Resource Exhaustion**

- The number of threads is limited, and if there are many client connections, the server might run out of thread resources or exceed the operating system's thread limit.
- This could cause the server to fail to respond to new client connections and even crash.

### 3. **Frequent Context Switching**

- The operating system needs to allocate time slices for each thread. If the number of threads is large, the CPU will frequently switch between threads.
- Context switching introduces performance overhead, reducing the actual processing efficiency.

### 4. **Uncontrolled Concurrency**

- Without a thread pool, the server cannot control the number of clients being processed concurrently.
- If a large number of clients suddenly connect, the server may be overwhelmed and unable to handle all the connections properly.

### 5. **Increased Code Complexity**

- The server needs to manually manage each thread's lifecycle, including handling thread termination, exceptions, and other concerns.
- This increases code complexity and maintenance costs.

---

## Advantages of Using a Thread Pool

A thread pool helps avoid the above issues:

1. **Thread Reuse**:
    - A thread pool reuses existing threads rather than creating a new thread for each client, reducing the overhead of thread creation and destruction.
2. **Concurrency Limitation**:
    - The thread pool allows

you to set a maximum number of threads, limiting the number of clients being processed concurrently and preventing resource exhaustion.
3. **Reduced Context Switching**:
    - By limiting the number of threads, context switching is reduced, improving processing efficiency.
4. **Simplified Management**:
    - The thread pool automatically manages the lifecycle of threads, so developers don't need to manually handle thread creation and destruction, simplifying the code.

---

## Conclusion

Without a thread pool, the server's concurrency performance would be severely impacted, and it may crash due to resource exhaustion. A thread pool improves server performance and stability by limiting concurrency, reusing threads, and reducing overhead. Therefore, using a thread pool is a best practice when developing servers that need to handle a large number of client connections.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.