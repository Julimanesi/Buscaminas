import java.awt.*;
import java.awt.event.*;
import javax.swing.JApplet;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;




public class Buscaminas extends  JFrame implements ActionListener{

	public static void main(String args[]) 
    {
        final Buscaminas f = new Buscaminas(9,10);
        f.setLocation(400, 250);
        f.setVisible(true);
    }
	
	PanelBuscaminas busc;
	Cara cara=new Cara();
	JTextField numCas=new JTextField("9"), numMina=new JTextField("10");
	int tamaño=20,casilla,mina;
	Buscaminas(int casilla,int mina)
	{
		super("Buscaminas");
		this.casilla=casilla;
		this.mina=mina;
		add(numCas);add(numMina);numCas.setBounds(10, 5, 20, 20);numMina.setBounds(40, 5, 20, 20);
		numCas.addActionListener(this);numMina.addActionListener(this);
		busc=new PanelBuscaminas(casilla,mina);
		busc.setLayout(null);
		this.setSize(20*casilla+15, 20*casilla+37+40);
		//this.setMaximumSize(this.getSize());this.setMinimumSize(getSize());
		this.add(cara);cara.addActionListener(this);
		this.add(busc);
		cara.setLocation(this.getSize().width/2-15, 0);
		
		//this.setContentPane(busc);
		 setDefaultCloseOperation(EXIT_ON_CLOSE);
	}
	@Override
	public void actionPerformed(ActionEvent event)
	{
		
		if(event.getSource()==cara)
		{//para ingresar las casillas
			boolean esNumero =true,cambio=false;//cambio registra un cambio en el tama�o de la ventana
			try{Integer.parseInt(numCas.getText());}catch(NumberFormatException e){ esNumero =false;}//si se puede hacer el cast
			if(esNumero && Integer.parseInt(numCas.getText())<=25 && Integer.parseInt(numCas.getText())>3 && casilla!=Integer.parseInt(numCas.getText()))//pregunta si es numero y si ese numero esta entre 4 y 25, y si es diferente del numero que ya estaba 
				{casilla=Integer.parseInt(numCas.getText());cambio=true;}
			else
				numCas.setText(""+casilla);
			
			//para ingresar las minas
			esNumero =true;
			try{Integer.parseInt(numMina.getText());}catch(NumberFormatException e){ esNumero =false;}
			if(esNumero && Math.pow(casilla,2)>Integer.parseInt(numMina.getText()))//pregunta si es numero y si ese numero no es mayor al numero de casillas
				mina=Integer.parseInt(numMina.getText());
			else
				numMina.setText(""+mina);
			
			//regenera el plano con las nuevas casillas y minas
			busc.removeAll();
			this.remove(busc);
			busc=new PanelBuscaminas(casilla,mina);
			if(cambio){
				this.setSize(20*casilla+15, 20*casilla+77);//cambio el tama�o de la ventana
				busc.setLayout(null);//muy importante!!! sin esto las casillas no se ubican bien
			}
			this.add(busc);
		}
		
	}
}


class PanelBuscaminas extends JPanel implements ActionListener
{	
	private Perder perder=new Perder("Lo siento, Perdio");//mensaje de perdio
	private Ganar ganar=new  Ganar("Felicidades, Gan�");//mensaje de gano
	private Label aux =new Label();
	private Button[][] botones ;
	private Casilla[][] etiq;
	private Point punto=new Point(0,40);//ubica todas las casillas en el lugar correspondiente
	private int tamaño=20,casilla,mina;
	PanelBuscaminas(int casilla,int mina)
	{
		this.casilla=casilla;
		this.mina=mina;
		botones =new Button[casilla][casilla];
		etiq= new Casilla[casilla][casilla];
		//this.setSize(20*casilla+15, 20*casilla+37);
		for (int i = 0; i < casilla; i++) 
		{
			for (int j = 0; j < casilla; j++) 
			{
				botones[i][j]=new Button();etiq[i][j]=new Casilla();
				botones[i][j].setBackground(Color.getHSBColor(0.67f, 0.6f, 0.95f));
				botones[i][j].setBounds(punto.x, punto.y, tamaño,tamaño);etiq[i][j].setBounds(punto.x, punto.y, tamaño,tamaño);
				add(botones[i][j]);add(etiq[i][j]);
				botones[i][j].addActionListener(this);
				punto.y+=tamaño;
				
			}
			punto.x+=tamaño;
			punto.y=40;
		}
		aux.setBounds(160, 160,20,20);
		add(aux);
		//coloca las minas en lugares aleatorios
		int x,y;
		for (int i = 0; i < mina; i++)
		{
			do{
			x=(int)(Math.random()*casilla);
			y=(int)(Math.random()*casilla);
			}while(etiq[x][y].hayMina());
			etiq[x][y].ponerMina();
		}
		//pone los numeros en las casillas
		for (int i = 0; i < casilla; i++) 
		{
			for (int j = 0; j < casilla; j++) 
			{
				if(i>0){
					etiq[i][j].hayMinaCerca(etiq[i-1][j]);
					if(j<casilla-1)
					{etiq[i][j].hayMinaCerca(etiq[i-1][j+1]);}
					if(j>0){etiq[i][j].hayMinaCerca(etiq[i-1][j-1]);}
				}
				if(j>0)
				{
					etiq[i][j].hayMinaCerca(etiq[i][j-1]);
					if(i<casilla-1)
					etiq[i][j].hayMinaCerca(etiq[i+1][j-1]);
				}
				if(j<casilla-1)
				{
					etiq[i][j].hayMinaCerca(etiq[i][j+1]);
					if(i<casilla-1)
					etiq[i][j].hayMinaCerca(etiq[i+1][j+1]);
				}
				if(i<casilla-1){etiq[i][j].hayMinaCerca(etiq[i+1][j]);}
			}
		}
		
	}
	
	@Override
	public void actionPerformed(ActionEvent event)
	{
		for (int i = 0; i < casilla; i++) 
		{
			for (int j = 0; j < casilla; j++) 
			{
				if(event.getSource() == botones[i][j])
				{
					botones[i][j].setVisible(false);
					if(etiq[i][j].hayMina())//si hace click en una mina
					{
						perder.setBounds(350, 300, 250,100 );
						perder.setVisible(true); mostrarMinas();
						desabilitarBotones();
					}
					if(gano())//si descubre todos los botones excepto donde hay una mina
					{
						ganar.setBounds(350, 300, 250,100 );
						ganar.setVisible(true);desabilitarBotones();
					}
					verCasillasCercanas(i,j);
				}
			}
		}	
	}
	public void mostrarMinas()
	{
		for (int i = 0; i < casilla; i++) 
		{
			for (int j = 0; j < casilla; j++) 
			{
				if(etiq[i][j].hayMina())
				{
					botones[i][j].setVisible(false);
				}
			}
		}
	}
	
	public void desabilitarBotones()
	{
		for (int i = 0; i < casilla; i++) 
		{
			for (int j = 0; j < casilla; j++) 
			{
				botones[i][j].setEnabled(false);
			}
		}
	}
	public boolean gano()
	{
		int cont=0;
		for (int i = 0; i < casilla; i++) 
		{
			for (int j = 0; j < casilla; j++) 
			{
				if(!botones[i][j].isVisible())
				{
					cont++;
				}
			}
		}
		return cont==(casilla*casilla-mina);
	}
	
	public void verCasillasCercanas(int i, int j)
	{
		int x,y;
		x=i;y=j;
		while(etiq[x++][y++].getText()=="" && x<casilla && y<casilla){botones[x][y].setVisible(false);}
		x=i;y=j;
		while(etiq[x--][y--].getText()=="" && x>=0 && y>=0){botones[x][y].setVisible(false);}
		x=i;y=j;
		while(etiq[x++][y--].getText()=="" && x<casilla && y>0){botones[x][y].setVisible(false);}
		x=i;y=j;
		while(etiq[x--][y++].getText()=="" && x>=0 && y<casilla){botones[x][y].setVisible(false);}
		x=i;y=j;
		while(etiq[x][y--].getText()==""  && y>=0){botones[x][y].setVisible(false);}
		x=i;y=j;
		while(etiq[x][y++].getText()==""  && y<casilla){botones[x][y].setVisible(false);}
		x=i;y=j;
		while(etiq[x--][y].getText()=="" && x>=0 ){botones[x][y].setVisible(false);}
		x=i;y=j;
		while(etiq[x++][y].getText()=="" && x<casilla ){botones[x][y].setVisible(false);}
		/*for (int x=0; x < casilla; x++) 
		{
			for (int y=0; y < casilla; y++) 
			{
				if(etiq[x][y].getText()=="" )
				{
					botones[x][y].setVisible(false);
				}
			}
		}*/
	}
}



class Casilla extends Label
{
	private boolean mina=false;
	private int minasCerca=0;
	private Color color=Color.black;
	public Casilla()
	{	
	}
	public void ponerMina()
	{
		mina=true;
		//this.setAlignment(Label.CENTER);
		//this.setText("M");
		repaint();
	}
	
	public boolean hayMina()
	{
		return mina;
	}
	public void hayMinaCerca(Casilla c)
	{
		if(!this.mina && c.hayMina())
		{
			minasCerca++;
			this.setAlignment(Label.CENTER);
			this.setText(""+minasCerca);
			switch(minasCerca){
	        case 1:color=Color.getHSBColor(0.67f, 0.6f, 0.95f);break;
	        case 2:color=Color.GREEN;break;
	        case 3:color=Color.RED;break;
	        case 4:color=Color.BLACK;break;
	        case 5:color=Color.ORANGE;break;
	        case 6:color=Color.YELLOW;break;
	        case 7:color=Color.PINK;break;
	        case 8:color=Color.MAGENTA;break;
	        default: color=Color.BLACK;
	        }
		}	
	}
	public void paint(Graphics g)
	{
        g.drawRect(0, 0, getSize().width, getSize().height);
        if(mina)
        {
        	/*ImageIcon minaImag=new ImageIcon("buscaminas.jpg");
        	g.drawImage(minaImag.getImage(), 0, 0,this );*/
        	g.fillOval(1, 1, getSize().width-2, getSize().height-2);
        }
        g.setColor(color);
        g.drawString(this.getText(), 7, 14);
	}
}


class Perder extends JFrame
{
	public Perder(String titulo)
	{
		super(titulo);
		this.setBounds(350, 300, 250,100 );
	}
	public void paint(Graphics g)
	{
		g.setColor(Color.yellow);
		g.fillOval(95, 50, 30, 30);
		g.setColor(Color.black);
		g.drawOval(95, 50, 30, 30);
		g.fillOval(100, 55, 10, 10);
		g.fillOval(112, 55, 10, 10);
		g.drawArc(99, 70, 22, 11, 180, -180);
	}
}

class Ganar extends JFrame
{
	
	public Ganar(String titulo)
	{
		super(titulo);
		this.setBounds(350, 300, 250,100 );
	}
	public void paint(Graphics g)
	{
		g.setColor(Color.yellow);
		g.fillOval(95, 50, 30, 30);
		g.setColor(Color.black);
		g.drawOval(95, 50, 30, 30);
		g.fillOval(100, 55, 10, 10);
		g.fillOval(112, 55, 10, 10);
		g.drawArc(99, 65, 22, 12, 180, 180);
	}
}

class Cara extends Button
{
	
	public Cara()
	{
		this.setSize(32, 32);
	}
	public void paint(Graphics g)
	{
		g.setColor(Color.yellow);
		g.fillOval(0, 0, 30, 30);//95,50
		g.setColor(Color.black);
		g.drawOval(0, 0, 30, 30);//95,50
		g.fillOval(5, 5, 10, 10);//100,55
		g.fillOval(17, 5, 10, 10);//112,55
		g.drawArc(4, 15, 22, 12, 180, 180);//99,65
	}
}
