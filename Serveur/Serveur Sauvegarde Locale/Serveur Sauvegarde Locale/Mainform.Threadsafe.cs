using System;
using System.Diagnostics;
using System.Drawing;

namespace Serveur_Sauvegarde_Locale
{
    partial class Mainform
    {
        public const string FILE_HEADER = "CommandLine Front End - L.Pilloni";
        public const string FILE_VERSION = "Version 0.2";
        public const string ERROR_PREFIX = "[lpi.cmdlinefrontend.prefix.error]";
        public const string MESSAGE_PREFIX = "[lpi.cmdlinefrontend.prefix.message]";

        // Thread-safe method of appending text to the console box
        delegate void AppendTextDelegate(string text);
        private void AppendConsoleText(string text)
        {
            // Use a delegate if called from a different thread,
            // else just append the text directly
            if (consoleRichTextBox.IsHandleCreated)
            {
                if (this.consoleRichTextBox.InvokeRequired)
                {
                    consoleRichTextBox.Invoke(new AppendTextDelegate(this.AppendConsoleText), new object[] { text });
                }
                else
                {
                    try
                    {
                        if (text.StartsWith(ERROR_PREFIX))
                        {
                            consoleRichTextBox.SelectionStart = consoleRichTextBox.TextLength;
                            consoleRichTextBox.SelectionLength = 0;

                            consoleRichTextBox.SelectionColor = Color.Coral;
                            consoleRichTextBox.AppendText(text.Substring(ERROR_PREFIX.Length));
                            consoleRichTextBox.SelectionColor = consoleRichTextBox.ForeColor;
                        }
                        else
                        /*
                            if (text.StartsWith(MESSAGE_PREFIX))
                        {
                            consoleRichTextBox.SelectionStart = consoleRichTextBox.TextLength;
                            consoleRichTextBox.SelectionLength = 0;

                            consoleRichTextBox.SelectionColor = Color.LightGreen;
                            consoleRichTextBox.AppendText(text.Substring(MESSAGE_PREFIX.Length));
                            consoleRichTextBox.SelectionColor = consoleRichTextBox.ForeColor;
                        }
                        else*/
                            this.consoleRichTextBox.AppendText(text);

                        this.ScrollConsoleToCaret();
                    }
                    catch (Exception e)
                    {
                        this.consoleRichTextBox.Clear();
                        this.AppendConsoleText(ERROR_PREFIX + e.Message);
                        this.AppendConsoleText(text);
                    }

                }
            }
        }

        // Thread-safe method of appending text to the console box
        delegate void ScrollToCaretDelegate();
        async private void ScrollConsoleToCaret()
        {
            // Use a delegate if called from a different thread,
            // else just append the text directly
            if (this.consoleRichTextBox.InvokeRequired)
            {
                // Application crashes when this line is executed
                consoleRichTextBox.Invoke(new ScrollToCaretDelegate(this.ScrollConsoleToCaret), null);
            }
            else
            {
                consoleRichTextBox?.ScrollToCaret();
            }
        }
        /// <summary>
		/// Ecrit une ligne sur la console et scrolle pour qu'elle soit visible
		/// </summary>
		/// <param name="text"></param>
		async static public void WriteToConsole(string text)
        {
            _instance?.AppendConsoleText(text + Environment.NewLine);
        }

        /// <summary>
        /// Ecrit une erreur sur la console
        /// </summary>
        /// <param name="text"></param>
        async static public void WriteErrorToConsole(string text)
        {
            WriteToConsole(ERROR_PREFIX + text);
            Debug.WriteLine("ERROR: " + text);
        }


        /// <summary>
        /// Afficher une exception dans la console
        /// </summary>
        /// <param name="e"></param>
        async public static void WriteExceptionToConsole(Exception e)
        {
            WriteErrorToConsole("!Exception raised: " + e.Message);
            WriteErrorToConsole(e.StackTrace);
            Debug.WriteLine("ERROR: " + e.Message);
            Debug.WriteLine("ERROR: " + e.StackTrace);
        }

        /// <summary>
        /// Ecrit un message sur la console
        /// </summary>
        /// <param name="text"></param>
        async public static void WriteMessageToConsole(string text)
        {
            //WriteToConsole(MESSAGE_PREFIX + text);
            WriteToConsole(text);
            //Debug.WriteLine(text);
        }

        delegate void UpdateRequetesDelegate();
        internal async static void UpdateNbRequetes()
        {
            if (_instance.InvokeRequired)
            {
                // Application crashes when this line is executed
                _instance.Invoke(new UpdateRequetesDelegate(UpdateNbRequetes), null);
            }
            else
            {
                _instance.toolStripStatusNbrequetes.Text = $"{RequestProcessor.nbRequetes} requête(s) en cours";
            }
        }
    }
}