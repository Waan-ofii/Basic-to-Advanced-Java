import java.net.*;
import java.io.*;

public class TCPServer
{
    public static void main(String[] args)
            throws Exception
    {
        ServerSocket server =
                new ServerSocket(5000);

        System.out.println("Waiting...");

        Socket client =
                server.accept();

        DataInputStream in =
                new DataInputStream(
                        client.getInputStream());

        DataOutputStream out =
                new DataOutputStream(
                        client.getOutputStream());

        String name =
                in.readUTF();

        out.writeUTF(
                "Student: "+name+
                        "\nDepartment: CS"
        );

        client.close();
        server.close();
    }
}