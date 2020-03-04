using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Drawing;
using System.Data;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;

namespace Serveur_Sauvegarde_Locale
{
    public partial class ToggleButton : CheckBox
    {
        private bool _horizontal = true;
        private StringFormat _alignment;

        [Description("Orientation horizontale ou verticale"), Category("Data")]
        public bool Horizontal
        {
            get { return _horizontal; }
            set { _horizontal = value; Invalidate(); }
        }

        public ContentAlignment TextAlign
        {
            get { return base.TextAlign; }
            set { base.TextAlign = value;
                createAlignement();
                
                Invalidate();
            }
        }

        private void createAlignement()
        {
            _alignment = new StringFormat();
            _alignment.FormatFlags = StringFormatFlags.NoWrap;
            _alignment.Trimming = StringTrimming.EllipsisCharacter;

            switch (base.TextAlign)
            {
                case ContentAlignment.TopLeft:
                    _alignment.Alignment = StringAlignment.Near;
                    _alignment.LineAlignment = StringAlignment.Near;
                    break;
                case ContentAlignment.TopCenter:
                    _alignment.Alignment = StringAlignment.Center;
                    _alignment.LineAlignment = StringAlignment.Near;
                    break;
                case ContentAlignment.TopRight:
                    _alignment.Alignment = StringAlignment.Far;
                    _alignment.LineAlignment = StringAlignment.Near;
                    break;
                case ContentAlignment.MiddleLeft:
                    _alignment.Alignment = StringAlignment.Near;
                    _alignment.LineAlignment = StringAlignment.Center;
                    break;
                case ContentAlignment.MiddleRight:
                    _alignment.Alignment = StringAlignment.Far;
                    _alignment.LineAlignment = StringAlignment.Center;
                    break;
                case ContentAlignment.MiddleCenter:
                    _alignment.Alignment = StringAlignment.Center;
                    _alignment.LineAlignment = StringAlignment.Center;
                    break;
                case ContentAlignment.BottomLeft:
                    _alignment.Alignment = StringAlignment.Near;
                    _alignment.LineAlignment = StringAlignment.Far;
                    break;
                case ContentAlignment.BottomCenter:
                    _alignment.Alignment = StringAlignment.Center;
                    _alignment.LineAlignment = StringAlignment.Far;
                    break;
                case ContentAlignment.BottomRight:
                    _alignment.Alignment = StringAlignment.Far;
                    _alignment.LineAlignment = StringAlignment.Far;
                    break;
            }
        }

        protected override void OnPaint(PaintEventArgs pevent)
        {
            pevent.Graphics.SmoothingMode = System.Drawing.Drawing2D.SmoothingMode.HighQuality;
            pevent.Graphics.InterpolationMode = System.Drawing.Drawing2D.InterpolationMode.HighQualityBicubic;
            pevent.Graphics.TextRenderingHint = System.Drawing.Text.TextRenderingHint.AntiAlias;


            using (Brush b = new SolidBrush(BackColor))
                pevent.Graphics.FillRectangle(b, ClientRectangle);

            Rectangle rControl = new Rectangle(ClientRectangle.Location, ClientRectangle.Size);
            rControl.Inflate(-1, -1);


            Rectangle rSwitch = calculeRectSwitch();
            Rectangle rEllipse = calculeRectEllipse(rSwitch);
            if (Checked)
            {
                pevent.Graphics.FillRectangle(SystemBrushes.Highlight, rSwitch);
                //pevent.Graphics.DrawRectangle(SystemPens.ButtonShadow, rSwitch);
            }
            else
            {
                pevent.Graphics.FillRectangle(SystemBrushes.ControlLight, rSwitch);
                //pevent.Graphics.DrawRectangle(SystemPens.ActiveBorder, rSwitch);
            }

            pevent.Graphics.FillEllipse(SystemBrushes.Window, rEllipse);

            Rectangle rTexte = new Rectangle(rSwitch.Right, ClientRectangle.Top, ClientRectangle.Width - rSwitch.Width, ClientRectangle.Height);
            rTexte.Inflate(-2, -2);
            if (_alignment == null)
                createAlignement();
            using (Brush b = new SolidBrush(ForeColor))
                pevent.Graphics.DrawString(Text, Font, b, rTexte, _alignment);
        }

        private Rectangle calculeRectEllipse(Rectangle rSwitch)
        {
            Rectangle res;
            int taille = Math.Min(rSwitch.Height, rSwitch.Width);
            if (_horizontal)
            {
                if (Checked)
                {
                    res = new Rectangle(rSwitch.Right - taille, rSwitch.Top, taille, taille);
                    res.Inflate(-2, -2);
                }
                else
                {
                    res = new Rectangle(rSwitch.Left, rSwitch.Top, taille, taille);
                    res.Inflate(-4, -4);
                }
            }
            else
            {
                if (Checked)
                {
                    res = new Rectangle(rSwitch.Left, rSwitch.Bottom - taille, taille, taille);
                    res.Inflate(-2, -2);
                }
                else
                {
                    res = new Rectangle(rSwitch.Left, rSwitch.Top, taille, taille);
                    res.Inflate(-4, -4);
                }

            }
            
            return res;
        }

        private Rectangle calculeRectSwitch()
        {
            Rectangle res;
            if (_horizontal)
            {
                int largeurSwitch = Math.Min(ClientRectangle.Width / 2, ClientRectangle.Height * 3);
                res = new Rectangle(ClientRectangle.Left, ClientRectangle.Top, largeurSwitch, ClientRectangle.Height);
                res.Inflate(-1, -1);
            }
            else
            {
                int largeurSwitch = Math.Min(ClientRectangle.Height / 2, ClientRectangle.Width * 3);
                res = new Rectangle(ClientRectangle.Left, ClientRectangle.Top, largeurSwitch, ClientRectangle.Height);
                res.Inflate(-1, -1);
            }

            return res;
        }
    }
}
