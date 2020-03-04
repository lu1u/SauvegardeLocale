using System;
using System.Net;
using System.Net.Sockets;
using System.Text;
using System.Threading;

namespace testserveur
{
    class Program
    {
        private const string TEST_ADRESSE = "TEST_ADRESSE";
        static char[] TRIM = { '\0' };
        private static TcpListener tcpListener;

        private static void HandleClientComm(object client)
        {
            TcpClient tcpClient = (TcpClient)client;
            Console.WriteLine("Connexion depuis " + tcpClient.ToString());
            NetworkStream clientStream = tcpClient.GetStream();

            byte[] message = new byte[4096];
            int bytesRead;

            while (true)
            {
                bytesRead = 0;

                try
                {
                    //blocks until a client sends a message
                    bytesRead = clientStream.Read(message, 0, 4096);


                    if (bytesRead == 0)
                    {
                        //the client has disconnected from the server
                        break;
                    }

                    //message has successfully been received
                    ASCIIEncoding encoder = new ASCIIEncoding();
                    //System.Diagnostics.Debug.WriteLine(encoder.GetString(message, 0, bytesRead));
                    string Input = (encoder.GetString(message, 0, bytesRead));
                    Input = Input.Trim();
                    Input = Input.Trim(TRIM);
                    object[] obj = new object[1];
                    obj[0] = Input;

                    if (TEST_ADRESSE.Equals(Input))
                    {
                        byte[] buffer = encoder.GetBytes("O\0");
                        clientStream.Write(buffer, 0, buffer.Length);
                        clientStream.Flush();
                    }
                }
                catch
                {
                    //a socket error has occured
                    break;
                }

            }

            tcpClient.Close();
        }
        public static void Main(string[] args)
        {
            TcpListener tcpListener = null;
            IPAddress ipAddress = IPAddress.Parse("192.168.1.32");

            try
            {
                // Set the listener on the local IP address 
                // and specify the port.
                tcpListener = new TcpListener(ipAddress, 50566);

                tcpListener.Start();
                while (true)
                {
                    Console.WriteLine("The server is running at port 50566...");
                    Console.WriteLine("The local End point is  " +
                                      tcpListener.LocalEndpoint);
                    Console.WriteLine("Waiting for a connection...");
                    //blocks until a client has connected to the server
                    TcpClient client = tcpListener.AcceptTcpClient();

                    //create a thread to handle communication
                    //with connected client
                    Thread clientThread = new Thread(new ParameterizedThreadStart(HandleClientComm));
                    clientThread.Start(client);
                }
                /*
                
                
                Socket s = tcpListener.AcceptSocket();
                Console.WriteLine("Connection accepted from " + s.RemoteEndPoint);

                s.r
                */
            }
            catch (Exception e)
            {
                Console.WriteLine("Error: " + e.ToString());
            }
        }

    }
}
