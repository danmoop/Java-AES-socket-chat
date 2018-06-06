import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server
{
    static ServerSocket serverSocket;

    public static List<Socket> allTheClients = new ArrayList<>();

    public static void main(String[] args) throws IOException
    {
        serverSocket = new ServerSocket(3003);

        print("Server started on " + serverSocket.getInetAddress().getHostAddress());

        while(true)
        {
            Socket s = serverSocket.accept();

            if(s.isConnected())
            {
                print("User connected from " + s.getInetAddress().getHostAddress());
            }

            ServerThread sThread = new ServerThread(s);

            sThread.start();
        }
    }

    private static void print(Object msg)
    {
        System.out.println(msg);
    }

    private static void printl(Object msg)
    {
        System.out.print(msg);
    }
}

class ServerThread extends Thread
{
    Socket client;
    DataInputStream inputStream;
    DataOutputStream outputStream;

    public ServerThread(Socket s) throws IOException
    {
        client = s;

        Server.allTheClients.add(s);


        InputStream sin = s.getInputStream();
        OutputStream sout = s.getOutputStream();

        inputStream = new DataInputStream(sin);
        outputStream = new DataOutputStream(sout);

        print(Server.allTheClients);
    }

    @Override
    public void run()
    {
        String message;

        try{
            while(true)
            {
                message = inputStream.readUTF();
                print(message);

                for(Socket client: Server.allTheClients)
                {
                    DataOutputStream oss = new DataOutputStream(client.getOutputStream());
                    oss.writeUTF(message);
                }
            }
        } catch (Exception e){

            print("User has disconnected from " + client.getInetAddress().getHostAddress());
            Server.allTheClients.remove(client);
            print(Server.allTheClients);

            for(Socket client: Server.allTheClients)
            {
                try {
                    DataOutputStream oss = new DataOutputStream(client.getOutputStream());
                    oss.writeUTF("/serverCommand>>" + client.getInetAddress().getHostAddress() + " has disconnected.");
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    private static void print(Object msg)
    {
        System.out.println(msg);
    }

    private static void printl(Object msg)
    {
        System.out.print(msg);
    }
}