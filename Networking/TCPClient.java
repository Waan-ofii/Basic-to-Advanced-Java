import java.net.*;
import java.io.*;

public class TCPClient
{
    public static void main(String[] args)
            throws Exception
    {
        Socket socket =
                new Socket("localhost",5000);

        DataOutputStream out =
                new DataOutputStream(
                        socket.getOutputStream());

        DataInputStream in =
                new DataInputStream(
                        socket.getInputStream());

        out.writeUTF("John");

        String response =
                in.readUTF();

        System.out.println(response);

        socket.close();
    }
}