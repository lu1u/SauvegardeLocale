namespace Serveur_Sauvegarde_Locale
{
    partial class Mainform
    {
        /// <summary>
        /// Variable nécessaire au concepteur.
        /// </summary>
        private System.ComponentModel.IContainer components = null;

        /// <summary>
        /// Nettoyage des ressources utilisées.
        /// </summary>
        /// <param name="disposing">true si les ressources managées doivent être supprimées ; sinon, false.</param>
        protected override void Dispose(bool disposing)
        {
            if (disposing && (components != null))
            {
                components.Dispose();
            }
            base.Dispose(disposing);
        }

        #region Code généré par le Concepteur Windows Form

        /// <summary>
        /// Méthode requise pour la prise en charge du concepteur - ne modifiez pas
        /// le contenu de cette méthode avec l'éditeur de code.
        /// </summary>
        private void InitializeComponent()
        {
            this.components = new System.ComponentModel.Container();
            System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(Mainform));
            this.consoleRichTextBox = new System.Windows.Forms.RichTextBox();
            this.label1 = new System.Windows.Forms.Label();
            this.textBoxAdresse = new System.Windows.Forms.TextBox();
            this.label2 = new System.Windows.Forms.Label();
            this.label3 = new System.Windows.Forms.Label();
            this.textBoxRepertoire = new System.Windows.Forms.TextBox();
            this.buttonOpenDlg = new System.Windows.Forms.Button();
            this.checkBox1 = new System.Windows.Forms.CheckBox();
            this.notifyIcon = new System.Windows.Forms.NotifyIcon(this.components);
            this.folderBrowserDialog = new System.Windows.Forms.FolderBrowserDialog();
            this.numericUpDownPort = new System.Windows.Forms.NumericUpDown();
            this.buttonStartStop = new System.Windows.Forms.Button();
            this.statusStrip1 = new System.Windows.Forms.StatusStrip();
            this.toolStripStatusLabelNbRequetes = new System.Windows.Forms.ToolStripStatusLabel();
            this.toolStripStatusNbrequetes = new System.Windows.Forms.ToolStripStatusLabel();
            ((System.ComponentModel.ISupportInitialize)(this.numericUpDownPort)).BeginInit();
            this.statusStrip1.SuspendLayout();
            this.SuspendLayout();
            // 
            // consoleRichTextBox
            // 
            this.consoleRichTextBox.Anchor = ((System.Windows.Forms.AnchorStyles)((((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Bottom) 
            | System.Windows.Forms.AnchorStyles.Left) 
            | System.Windows.Forms.AnchorStyles.Right)));
            this.consoleRichTextBox.BackColor = System.Drawing.Color.Black;
            this.consoleRichTextBox.BorderStyle = System.Windows.Forms.BorderStyle.FixedSingle;
            this.consoleRichTextBox.DetectUrls = false;
            this.consoleRichTextBox.Font = new System.Drawing.Font("Lucida Console", 8.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.consoleRichTextBox.ForeColor = System.Drawing.Color.FromArgb(((int)(((byte)(128)))), ((int)(((byte)(255)))), ((int)(((byte)(128)))));
            this.consoleRichTextBox.Location = new System.Drawing.Point(1, 122);
            this.consoleRichTextBox.Name = "consoleRichTextBox";
            this.consoleRichTextBox.ReadOnly = true;
            this.consoleRichTextBox.ShortcutsEnabled = false;
            this.consoleRichTextBox.Size = new System.Drawing.Size(705, 224);
            this.consoleRichTextBox.TabIndex = 1;
            this.consoleRichTextBox.Text = "";
            // 
            // label1
            // 
            this.label1.AutoSize = true;
            this.label1.Location = new System.Drawing.Point(10, 14);
            this.label1.Name = "label1";
            this.label1.Size = new System.Drawing.Size(48, 13);
            this.label1.TabIndex = 2;
            this.label1.Text = "Adresse:";
            // 
            // textBoxAdresse
            // 
            this.textBoxAdresse.Location = new System.Drawing.Point(117, 10);
            this.textBoxAdresse.Name = "textBoxAdresse";
            this.textBoxAdresse.ReadOnly = true;
            this.textBoxAdresse.Size = new System.Drawing.Size(154, 20);
            this.textBoxAdresse.TabIndex = 3;
            this.textBoxAdresse.Text = "192.1.132 (PCMAISON)";
            this.textBoxAdresse.TextAlign = System.Windows.Forms.HorizontalAlignment.Center;
            // 
            // label2
            // 
            this.label2.AutoSize = true;
            this.label2.Location = new System.Drawing.Point(292, 14);
            this.label2.Name = "label2";
            this.label2.Size = new System.Drawing.Size(29, 13);
            this.label2.TabIndex = 4;
            this.label2.Text = "Port:";
            // 
            // label3
            // 
            this.label3.AutoSize = true;
            this.label3.Location = new System.Drawing.Point(10, 46);
            this.label3.Name = "label3";
            this.label3.Size = new System.Drawing.Size(100, 13);
            this.label3.TabIndex = 6;
            this.label3.Text = "Répertoire de base:";
            // 
            // textBoxRepertoire
            // 
            this.textBoxRepertoire.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left) 
            | System.Windows.Forms.AnchorStyles.Right)));
            this.textBoxRepertoire.Location = new System.Drawing.Point(117, 43);
            this.textBoxRepertoire.Name = "textBoxRepertoire";
            this.textBoxRepertoire.ReadOnly = true;
            this.textBoxRepertoire.Size = new System.Drawing.Size(532, 20);
            this.textBoxRepertoire.TabIndex = 7;
            this.textBoxRepertoire.TextChanged += new System.EventHandler(this.onTextboxRepertoireChanged);
            // 
            // buttonOpenDlg
            // 
            this.buttonOpenDlg.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Right)));
            this.buttonOpenDlg.Location = new System.Drawing.Point(655, 43);
            this.buttonOpenDlg.Name = "buttonOpenDlg";
            this.buttonOpenDlg.Size = new System.Drawing.Size(39, 23);
            this.buttonOpenDlg.TabIndex = 8;
            this.buttonOpenDlg.Text = "...";
            this.buttonOpenDlg.UseVisualStyleBackColor = true;
            this.buttonOpenDlg.Click += new System.EventHandler(this.onBrowseFolderButtonClic);
            // 
            // checkBox1
            // 
            this.checkBox1.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Right)));
            this.checkBox1.AutoSize = true;
            this.checkBox1.Checked = true;
            this.checkBox1.CheckState = System.Windows.Forms.CheckState.Checked;
            this.checkBox1.Location = new System.Drawing.Point(498, 13);
            this.checkBox1.Name = "checkBox1";
            this.checkBox1.Size = new System.Drawing.Size(196, 17);
            this.checkBox1.TabIndex = 9;
            this.checkBox1.Text = "Réduire dans la barre de notification";
            this.checkBox1.UseVisualStyleBackColor = true;
            // 
            // notifyIcon
            // 
            this.notifyIcon.BalloonTipText = "Serveur Sauvegarde Locale";
            this.notifyIcon.Icon = ((System.Drawing.Icon)(resources.GetObject("notifyIcon.Icon")));
            this.notifyIcon.Text = "Serveur sauvegarde locale";
            this.notifyIcon.DoubleClick += new System.EventHandler(this.onNotifyIconDoubleClic);
            // 
            // folderBrowserDialog
            // 
            this.folderBrowserDialog.Description = "Choisissez un répertoire racine pour la sauvegarde";
            // 
            // numericUpDownPort
            // 
            this.numericUpDownPort.Location = new System.Drawing.Point(327, 10);
            this.numericUpDownPort.Maximum = new decimal(new int[] {
            65536,
            0,
            0,
            0});
            this.numericUpDownPort.Minimum = new decimal(new int[] {
            1024,
            0,
            0,
            0});
            this.numericUpDownPort.Name = "numericUpDownPort";
            this.numericUpDownPort.Size = new System.Drawing.Size(120, 20);
            this.numericUpDownPort.TabIndex = 10;
            this.numericUpDownPort.ThousandsSeparator = true;
            this.numericUpDownPort.Value = new decimal(new int[] {
            50566,
            0,
            0,
            0});
            this.numericUpDownPort.ValueChanged += new System.EventHandler(this.onPortChanged);
            // 
            // buttonStartStop
            // 
            this.buttonStartStop.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left) 
            | System.Windows.Forms.AnchorStyles.Right)));
            this.buttonStartStop.FlatAppearance.BorderColor = System.Drawing.SystemColors.Highlight;
            this.buttonStartStop.FlatAppearance.BorderSize = 3;
            this.buttonStartStop.FlatAppearance.MouseOverBackColor = System.Drawing.SystemColors.HighlightText;
            this.buttonStartStop.FlatStyle = System.Windows.Forms.FlatStyle.Flat;
            this.buttonStartStop.Font = new System.Drawing.Font("Microsoft Sans Serif", 8.25F, System.Drawing.FontStyle.Bold, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.buttonStartStop.Location = new System.Drawing.Point(13, 74);
            this.buttonStartStop.Name = "buttonStartStop";
            this.buttonStartStop.Size = new System.Drawing.Size(681, 42);
            this.buttonStartStop.TabIndex = 11;
            this.buttonStartStop.Text = "Serveur démarré, cliquer pour l\'arrêter";
            this.buttonStartStop.UseVisualStyleBackColor = true;
            this.buttonStartStop.Click += new System.EventHandler(this.ButtonStartStop_Click);
            // 
            // statusStrip1
            // 
            this.statusStrip1.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.toolStripStatusLabelNbRequetes,
            this.toolStripStatusNbrequetes});
            this.statusStrip1.Location = new System.Drawing.Point(0, 349);
            this.statusStrip1.Name = "statusStrip1";
            this.statusStrip1.Size = new System.Drawing.Size(706, 22);
            this.statusStrip1.TabIndex = 12;
            this.statusStrip1.Text = "statusStrip1";
            // 
            // toolStripStatusLabelNbRequetes
            // 
            this.toolStripStatusLabelNbRequetes.Name = "toolStripStatusLabelNbRequetes";
            this.toolStripStatusLabelNbRequetes.Size = new System.Drawing.Size(0, 17);
            // 
            // toolStripStatusNbrequetes
            // 
            this.toolStripStatusNbrequetes.Name = "toolStripStatusNbrequetes";
            this.toolStripStatusNbrequetes.Size = new System.Drawing.Size(0, 17);
            // 
            // Mainform
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.ClientSize = new System.Drawing.Size(706, 371);
            this.Controls.Add(this.statusStrip1);
            this.Controls.Add(this.buttonStartStop);
            this.Controls.Add(this.numericUpDownPort);
            this.Controls.Add(this.checkBox1);
            this.Controls.Add(this.buttonOpenDlg);
            this.Controls.Add(this.textBoxRepertoire);
            this.Controls.Add(this.label3);
            this.Controls.Add(this.label2);
            this.Controls.Add(this.textBoxAdresse);
            this.Controls.Add(this.label1);
            this.Controls.Add(this.consoleRichTextBox);
            this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
            this.MinimumSize = new System.Drawing.Size(616, 189);
            this.Name = "Mainform";
            this.Text = "Sauvegarde locale";
            this.FormClosing += new System.Windows.Forms.FormClosingEventHandler(this.onMainformClosing);
            this.Load += new System.EventHandler(this.onMainFormLoad);
            this.Resize += new System.EventHandler(this.onMainFormResize);
            ((System.ComponentModel.ISupportInitialize)(this.numericUpDownPort)).EndInit();
            this.statusStrip1.ResumeLayout(false);
            this.statusStrip1.PerformLayout();
            this.ResumeLayout(false);
            this.PerformLayout();

        }

        #endregion

        private System.Windows.Forms.RichTextBox consoleRichTextBox;
        private System.Windows.Forms.Label label1;
        private System.Windows.Forms.TextBox textBoxAdresse;
        private System.Windows.Forms.Label label2;
        private System.Windows.Forms.Label label3;
        private System.Windows.Forms.TextBox textBoxRepertoire;
        private System.Windows.Forms.Button buttonOpenDlg;
        private System.Windows.Forms.CheckBox checkBox1;
        private System.Windows.Forms.NotifyIcon notifyIcon;
        private System.Windows.Forms.FolderBrowserDialog folderBrowserDialog;
        private System.Windows.Forms.NumericUpDown numericUpDownPort;
        private System.Windows.Forms.Button buttonStartStop;
        private System.Windows.Forms.StatusStrip statusStrip1;
        private System.Windows.Forms.ToolStripStatusLabel toolStripStatusLabelNbRequetes;
        private System.Windows.Forms.ToolStripStatusLabel toolStripStatusNbrequetes;
    }
}

