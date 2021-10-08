import javax.swing.*;
import javax.swing.border.*;
import java.math.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileView;
import javax.swing.filechooser.*;
import javax.swing.event.*;

public class SzyfrRsa
{
    public static void main(String[] args)
    {
        RamkaProgramu ramka = new RamkaProgramu();
        ramka.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ramka.show();
    }
}

class RamkaProgramu extends JFrame
{
    public RamkaProgramu()
    {
        setSize(500,300);
        setTitle("Szyfr RSA");
     
        
        PanelObslugi panel = new PanelObslugi();
        Container powZawartosci = getContentPane();
        powZawartosci.add(panel);
        
        try
        {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
            SwingUtilities.updateComponentTreeUI(RamkaProgramu.this);
        }
        catch(Exception wyj) {}
    
    }
}

class AlgorytmRsa 
{
    public AlgorytmRsa()
    {
    }
    public void generujKlucz()   //generuje nowy klucz czyli wszystkie liczby p q b
    {
        p = new BigInteger(dlugosc_klucza/2,test_prime,losuj);
        q = new BigInteger(dlugosc_klucza/2,test_prime,losuj);
        
        obliczN();
        obliczFi_N();
        
        do
        {
        	b= new BigInteger(dlugosc_klucza-1,test_prime,losuj);		//liczba b exponenta rownania  
        } while (b.compareTo(fi_n)>0);
        obliczA();
        isPubl = true;			//ustawia czy wlasciw klucze sa dostepne
        isPryw = true;
    }
    private void obliczN()   //oblicza skladnik n czyli p*q
    {
    	n = new BigInteger("0");
        n = p.multiply(q);
    }
    private void obliczFi_N()   //oblicza fi(n) = (p-1)*(q-1)
    {
        fi_n = new BigInteger("0");
        fi_n = (p.subtract(BigInteger.valueOf(1L))).multiply(q.subtract(BigInteger.valueOf(1L)));
    }
    private void obliczA()
    {
    	a= new BigInteger("0");
        a= b.modInverse(fi_n);
    }
    
    public void wczytajKluczPr(String nazwa_klucza) throws IOException		//wczytyje z pliku klucz prywatny: liczby p,q,b
    {													// i oblicza pozostale liczby : n,fi_n,a
		BufferedReader plik_key_pr = new BufferedReader(new FileReader(nazwa_klucza));
		p = new BigInteger(plik_key_pr.readLine());
		q = new BigInteger(plik_key_pr.readLine());
		b = new BigInteger(plik_key_pr.readLine());
		plik_key_pr.close();
		obliczN();
		obliczFi_N();
		obliczA();
		isPubl = true;
        isPryw = true;
    }
    public void zapiszKluczPr(String nazwa_klucza) throws IOException		//zapisuje do pliku klucz prywatny liczby: p,q,b
    {
		PrintWriter plik_key_pr = new PrintWriter(new FileOutputStream(nazwa_klucza));
		plik_key_pr.println(p);
		plik_key_pr.println(q);
		plik_key_pr.println(b);
		plik_key_pr.close();		
    }
    public void wczytajKluczPub(String nazwa_klucza) throws IOException		//wczytuje klucz publiczny liczby n,b
    {
		BufferedReader plik_key_pub = new BufferedReader(new FileReader(nazwa_klucza));
		n = new BigInteger(plik_key_pub.readLine());
		b = new BigInteger(plik_key_pub.readLine());
		plik_key_pub.close();
		p = new BigInteger("0");				//zeruje wszystkie pozostale skladniki
		q = new BigInteger("0");
		a = new BigInteger("0");
		fi_n = new BigInteger("0");
		isPubl = true;				
        isPryw = false;				
    }
    public void zapiszKluczPub(String nazwa_klucza) throws IOException		//zapisuje do pliku klucz publ lizcby: n,b
    {
    	PrintWriter plik_key_pub = new PrintWriter(new FileOutputStream(nazwa_klucza));
		plik_key_pub.println(n);
		plik_key_pub.println(b);
		plik_key_pub.close();		
    }
    /*metoda szyfruje wiadomosc zapisan¹ w pliku plik_we do pliku plik_wy */
    public void szyfruj(String nazwa_we,String nazwa_wy) throws IOException
    {
    	File we = new File(nazwa_we);
    	DataInputStream plik_we = new DataInputStream(new FileInputStream(we));
    							//File wy = new File("tekst.kod");
    							//DataOutputStream plik_wy = new DataOutputStream(new FileOutputStream(wy));
    	PrintWriter plik_wy = new PrintWriter(new FileOutputStream(nazwa_wy));
    	long rozmiar_pliku_we = we.length(); 
    	byte[] tablica = new byte[dlugosc_klucza/8];
      	long ile_razy = (rozmiar_pliku_we/(dlugosc_klucza/8))+1;
      	int ost_tabl = (int)rozmiar_pliku_we%(dlugosc_klucza/8);
        for (int j=0;j<ile_razy;j++)
        {
        	if (j==(ile_razy-1))
        	{
        		if (ost_tabl!=0) 
        		{
	        		byte[] tab_2 = new byte[ost_tabl];
	        		int i = plik_we.read(tab_2);
	        		x= new BigInteger(tab_2);
	        		if (x.signum()<0)
	        		{
	        			plik_wy.print("-|");
	        			x = x.negate();
	        		}
	        		y= new BigInteger("0");
	        		y= x.modPow(b,n);
	        		plik_wy.println(y);
	        				//plik_wy.write(tab_2);
        		}
        	}
        	else
        	{
        		int i = plik_we.read(tablica);
        		x= new BigInteger(tablica);
        		if (x.signum()<0)
        		{
        			plik_wy.print("-|");
        			x = x.negate();
        		}
        		y= new BigInteger("0");
        		y= x.modPow(b,n);
        		plik_wy.println(y);
        				//plik_wy.write(tablica);
        	}
    	}
    	plik_we.close();
    	plik_wy.close();
    }
    public void deszyfruj(String nazwa_we,String nazwa_wy) throws IOException
    {
    	BufferedReader plik_we = new BufferedReader(new FileReader(nazwa_we));
    	File wy = new File(nazwa_wy);
    	DataOutputStream plik_wy = new DataOutputStream(new FileOutputStream(wy));
    	String linia;
    	String linia_kon="";
    	while ((linia=plik_we.readLine())!= null)
    	{
    		StringTokenizer linia_dziel = new StringTokenizer(linia,"|");
    		if (linia_dziel.countTokens()>1)
    		{
    			if (linia_dziel.nextToken().charAt(0)=='-') isMinus=true;
    		}
    		linia_kon = linia_dziel.nextToken();
    		
    		y = new BigInteger(linia_kon);
    		x = new BigInteger("0");
    		x = y.modPow(a,n);
    		if (isMinus) 
    		{
    			x=x.negate();
    			isMinus=false;
    		}
    		int ile_bajt = (x.bitLength())/8;
    		if ((x.bitLength()%8)>0) ile_bajt++;
    		byte[] tablica = new byte[ile_bajt];
    		for (int i=0;i<tablica.length;i++) tablica[i]=0;
    		tablica = x.toByteArray();
    		plik_wy.write(tablica,0,tablica.length);
    	}
    	plik_we.close();
    	plik_wy.close();
    }
    public boolean isKeyPubl()
    {
    	return (isPubl);
    }
    public boolean isKeyPryw()
    {
    	return (isPryw);
    }
    public void resetujKlucz()
    {
    	q = new BigInteger("0");
		p = new BigInteger("0");
		n = new BigInteger("0");
		fi_n = new BigInteger("0");
		b = new BigInteger("0");
		a = new BigInteger("0");
		   	
    	isPubl = false;
    	isPryw = false;
    }
    private boolean isMinus = false;
    private boolean isPubl = false;
    private boolean isPryw = false;
    private Random losuj = new Random();
    private int test_prime = 5;
    private int dlugosc_klucza = 512;
    private BigInteger x ;
    private BigInteger y ;
    private BigInteger q,p,n,fi_n,b,a;
}

class PanelObslugi extends JPanel
{
	public PanelObslugi()
	{
		setLayout(new BorderLayout());
        JMenuBar pasekMenu = new JMenuBar();
        add(pasekMenu,BorderLayout.NORTH);
      
        JMenu menuOperacja = new JMenu("Operacja");
        ButtonGroup grupaRadio = new ButtonGroup();
        opSzyfrowanie = new JRadioButtonMenuItem("Szyfrowanie");
        opSzyfrowanie.setSelected(jestSzyfrowanie);
        opSzyfrowanie.addActionListener(new
        		ActionListener()
        		{
        			public void actionPerformed(ActionEvent zd)
        			{
        				jestSzyfrowanie = true;
        				przyciskSzyfrujDeszyfruj.setText(jestSzyfrowanie ? "Szyfruj" : "Deszyfruj");
        				zapisz_klucz_pr.setEnabled(!(jestSzyfrowanie));
                        zapisz_klucz_pub.setEnabled(!(jestSzyfrowanie));
                        wczyt_klucz_pr.setEnabled(!(jestSzyfrowanie));
                        wczyt_klucz_pub.setEnabled(jestSzyfrowanie);
                        szyfr.resetujKlucz();
                        tekstKlucz.setText("");
                        tekstZrodla.setText("");
                        tekstCel.setText("");
                        przyciskSzyfrujDeszyfruj.setEnabled(((szyfr.isKeyPubl() && jestSzyfrowanie) || (szyfr.isKeyPryw() && !(jestSzyfrowanie))) && (jestPlikZr && jestPlikC));
        			}
        		});
        opDeszyfrowanie = new JRadioButtonMenuItem("Deszyfrowanie");
        opDeszyfrowanie.setSelected(!(jestSzyfrowanie));
        opDeszyfrowanie.addActionListener(new
        		ActionListener()
        		{
        			public void actionPerformed(ActionEvent zd)
        			{
        				jestSzyfrowanie = false;
        				przyciskSzyfrujDeszyfruj.setText(jestSzyfrowanie ? "Szyfruj" : "Deszyfruj");
        				zapisz_klucz_pr.setEnabled(jestSzyfrowanie);
                        zapisz_klucz_pub.setEnabled(jestSzyfrowanie);
                        wczyt_klucz_pr.setEnabled(!(jestSzyfrowanie));
                        wczyt_klucz_pub.setEnabled(jestSzyfrowanie);
                        szyfr.resetujKlucz();
                        tekstKlucz.setText("");
                        tekstZrodla.setText("");
                        tekstCel.setText("");
                        przyciskSzyfrujDeszyfruj.setEnabled(((szyfr.isKeyPubl() && jestSzyfrowanie) || (szyfr.isKeyPryw() && !(jestSzyfrowanie))) && (jestPlikZr && jestPlikC));
        			}
        		});
        grupaRadio.add(opSzyfrowanie);
        grupaRadio.add(opDeszyfrowanie);
        menuOperacja.add(opSzyfrowanie);
        menuOperacja.add(opDeszyfrowanie);
        pasekMenu.add(menuOperacja);
        
        menuOperacja.addSeparator();
        JMenuItem zamknijProg = new JMenuItem("Zamknij");
        menuOperacja.add(zamknijProg);
        zamknijProg.addActionListener(new
        	ActionListener()
        	{
        		public void actionPerformed(ActionEvent zdarzenie)
        		{
        			System.exit(0);
        		}
        	});
        
        
        JMenu menuKlucz = new JMenu("Klucz");
        pasekMenu.add(menuKlucz);
        wczyt_klucz_pr = new JMenuItem("Wczytaj Klucz Prywatny");
        menuKlucz.add(wczyt_klucz_pr);
        wczyt_klucz_pr.addActionListener(new 
                ActionListener()
                {
                    public void actionPerformed(ActionEvent zdarzenie)
                    {
                    	int wyn = oknoWyboru.showDialog(PanelObslugi.this,"Wybierz");
						if (wyn==JFileChooser.APPROVE_OPTION)
						{
							tekstKlucz.setText(oknoWyboru.getSelectedFile().getName());
							sciezka_klucza = oknoWyboru.getSelectedFile().getPath();						
	                        try
							{
								szyfr.wczytajKluczPr(sciezka_klucza);
							}
							catch(IOException w) {}
						}
						przyciskSzyfrujDeszyfruj.setEnabled(((szyfr.isKeyPubl() && jestSzyfrowanie) || (szyfr.isKeyPryw() && !(jestSzyfrowanie))) && (jestPlikZr && jestPlikC));                    
                    }
                });
        zapisz_klucz_pr = new JMenuItem("Zapisz Klucz Prywatny");
        menuKlucz.add(zapisz_klucz_pr);
        zapisz_klucz_pr.addActionListener(new 
                ActionListener()
                {
                    public void actionPerformed(ActionEvent zdarzenie)
                    {
                        int wyn = oknoWyboru.showSaveDialog(PanelObslugi.this);
						if (wyn==JFileChooser.APPROVE_OPTION)
						{
							try
							{
								szyfr.zapiszKluczPr(oknoWyboru.getSelectedFile().getPath());
								zapisz_klucz_pr.setEnabled(false);
							}
							catch(IOException w) {}
							if (czy_klucz_zapisany)
							{
								//odwroc dziala ne gen klucz
								wczyt_klucz_pr.setEnabled(!(jestSzyfrowanie));
                        		wczyt_klucz_pub.setEnabled(jestSzyfrowanie);
		                        opSzyfrowanie.setEnabled(true);
		                        opDeszyfrowanie.setEnabled(true);
		                        przyciskSzyfrujDeszyfruj.setEnabled(((szyfr.isKeyPubl() && jestSzyfrowanie) || (szyfr.isKeyPryw() && !(jestSzyfrowanie))) && (jestPlikZr && jestPlikC));
							}
							else czy_klucz_zapisany = true;	
							
						}
                    }
                });
        menuKlucz.addSeparator();
        wczyt_klucz_pub = new JMenuItem("Wczytaj Klucz Publiczny");
        menuKlucz.add(wczyt_klucz_pub);
        wczyt_klucz_pub.addActionListener(new 
                ActionListener()
                {
                    public void actionPerformed(ActionEvent zdarzenie)
                    {
                    	int wyn = oknoWyboru.showDialog(PanelObslugi.this,"Wybierz");
						if (wyn==JFileChooser.APPROVE_OPTION)
						{
							tekstKlucz.setText(oknoWyboru.getSelectedFile().getName());
							sciezka_klucza = oknoWyboru.getSelectedFile().getPath();						
	                        try
							{
								szyfr.wczytajKluczPub(sciezka_klucza);
								
							}
							catch(IOException w) {}
						}
						przyciskSzyfrujDeszyfruj.setEnabled(((szyfr.isKeyPubl() && jestSzyfrowanie) || (szyfr.isKeyPryw() && !(jestSzyfrowanie))) && (jestPlikZr && jestPlikC));
                    }
                });

        zapisz_klucz_pub = new JMenuItem("Zapisz Klucz Publiczny");
        menuKlucz.add(zapisz_klucz_pub);
        zapisz_klucz_pub.addActionListener(new 
                ActionListener()
                {
                    public void actionPerformed(ActionEvent zdarzenie)
                    {
                    	int wyn = oknoWyboru.showSaveDialog(PanelObslugi.this);
						if (wyn==JFileChooser.APPROVE_OPTION)
						{
							try
							{
								szyfr.zapiszKluczPub(oknoWyboru.getSelectedFile().getPath());
								zapisz_klucz_pub.setEnabled(false);
							}
							catch(IOException w) {}
							if (czy_klucz_zapisany)
							{
								//odwroc dziala ne gen klucz
								wczyt_klucz_pr.setEnabled(!(jestSzyfrowanie));
                        		wczyt_klucz_pub.setEnabled(jestSzyfrowanie);
		                        opSzyfrowanie.setEnabled(true);
		                        opDeszyfrowanie.setEnabled(true);
		                        przyciskSzyfrujDeszyfruj.setEnabled(((szyfr.isKeyPubl() && jestSzyfrowanie) || (szyfr.isKeyPryw() && !(jestSzyfrowanie))) && (jestPlikZr && jestPlikC));
							}
							else czy_klucz_zapisany = true;	
							
						}
				    }
                });
        menuKlucz.addSeparator();
        gener_klucz = new JMenuItem("Generuj Nowy Klucz");
        menuKlucz.add(gener_klucz);
        gener_klucz.addActionListener(new 
                ActionListener()
                {
                    public void actionPerformed(ActionEvent zdarzenie)
                    {
                        szyfr.generujKlucz();
                        zapisz_klucz_pr.setEnabled(szyfr.isKeyPryw());
                        zapisz_klucz_pub.setEnabled(szyfr.isKeyPubl());
                        wczyt_klucz_pr.setEnabled(false);
                        wczyt_klucz_pub.setEnabled(false);
                        opSzyfrowanie.setEnabled(false);
                        opDeszyfrowanie.setEnabled(false);
                        przyciskSzyfrujDeszyfruj.setEnabled(false);
                        czy_klucz_zapisany = false;
                    }
                });    
    	zapisz_klucz_pr.setEnabled(!(jestSzyfrowanie));
        zapisz_klucz_pub.setEnabled(!(jestSzyfrowanie));
        wczyt_klucz_pr.setEnabled(!(jestSzyfrowanie));
        wczyt_klucz_pub.setEnabled(jestSzyfrowanie);
        

    	
    	
    	
    	
		JPanel panelPrzyciskow = new JPanel();
		przyciskSzyfrujDeszyfruj = new JButton(jestSzyfrowanie ? "Szyfruj" : "Deszyfruj");
		przyciskSzyfrujDeszyfruj.setEnabled(((szyfr.isKeyPubl() && jestSzyfrowanie) || (szyfr.isKeyPryw() && !(jestSzyfrowanie))) && (jestPlikZr && jestPlikC));
		przyciskSzyfrujDeszyfruj.addActionListener(new
				ActionListener()
				{
					public void actionPerformed(ActionEvent zdarzenie)
					{
						try
						{
							if (jestSzyfrowanie) szyfr.szyfruj(tekstZrodla.getText(),tekstCel.getText());
							else szyfr.deszyfruj(tekstZrodla.getText(),tekstCel.getText());
						}
						catch(IOException w) {}
					}
				});
		panelPrzyciskow.add(przyciskSzyfrujDeszyfruj);		
	   	add(panelPrzyciskow,BorderLayout.SOUTH);
    	
    	
    	// srodek okna
    	JPanel srodekOkna = new JPanel();
    	add(srodekOkna,BorderLayout.CENTER);
    	
    	   	
    	Border wytrawiona = BorderFactory.createEtchedBorder();
    	
    	JPanel panelKlucz = new JPanel();
    	Border nazwaKlucz = BorderFactory.createTitledBorder(wytrawiona,"Plik Klucza");
    	panelKlucz.setBorder(nazwaKlucz);
    	tekstKlucz = new JTextField(15);
    	tekstKlucz.setEditable(false);
    	panelKlucz.add(tekstKlucz);
    	srodekOkna.add(panelKlucz);
    	
    	JPanel panelZrodlo = new JPanel();
    	Border nazwaZrodlo = BorderFactory.createTitledBorder(wytrawiona,"Plik ród³owy");
    	panelZrodlo.setBorder(nazwaZrodlo);
    	tekstZrodla = new JTextField(30);
    	tekstZrodla.getDocument().addDocumentListener(new 
    				DocumentListener()
    				{
    					public void insertUpdate(DocumentEvent z) 
    					{
    						String tekst = tekstZrodla.getText();
    						if (tekst.equals("")) jestPlikZr=false;
    						else jestPlikZr=true;
    						przyciskSzyfrujDeszyfruj.setEnabled(((szyfr.isKeyPubl() && jestSzyfrowanie) || (szyfr.isKeyPryw() && !(jestSzyfrowanie))) && (jestPlikZr && jestPlikC));
    					}
    					public void removeUpdate(DocumentEvent z) 
    					{
    						String tekst = tekstZrodla.getText();
    						if (tekst.equals("")) jestPlikZr=false;
    						else jestPlikZr=true;
    						przyciskSzyfrujDeszyfruj.setEnabled(((szyfr.isKeyPubl() && jestSzyfrowanie) || (szyfr.isKeyPryw() && !(jestSzyfrowanie))) && (jestPlikZr && jestPlikC));
    					}
    					public void changedUpdate(DocumentEvent z) {}
    				});
    	JButton wybierzZrodlo = new JButton("Przegl¹daj");
    	panelZrodlo.add(tekstZrodla);
    	panelZrodlo.add(wybierzZrodlo);
    	srodekOkna.add(panelZrodlo);
    	wybierzZrodlo.addActionListener(new SluchaczWyboruPlikuZr());
    	
    	
    	JPanel panelCel = new JPanel();
    	Border nazwaCel = BorderFactory.createTitledBorder(wytrawiona,"Plik Docelowy");
    	panelCel.setBorder(nazwaCel);
    	tekstCel = new JTextField(30);
    	tekstCel.getDocument().addDocumentListener(new 
    				DocumentListener()
    				{
    					public void insertUpdate(DocumentEvent z) 
    					{
    						String tekst = tekstCel.getText();
    						if (tekst.equals("")) jestPlikC=false;
    						else jestPlikC=true;
    						przyciskSzyfrujDeszyfruj.setEnabled(((szyfr.isKeyPubl() && jestSzyfrowanie) || (szyfr.isKeyPryw() && !(jestSzyfrowanie))) && (jestPlikZr && jestPlikC));
    					}
    					public void removeUpdate(DocumentEvent z) 
    					{
    						String tekst = tekstCel.getText();
    						if (tekst.equals("")) jestPlikC=false;
    						else jestPlikC=true;
    						przyciskSzyfrujDeszyfruj.setEnabled(((szyfr.isKeyPubl() && jestSzyfrowanie) || (szyfr.isKeyPryw() && !(jestSzyfrowanie))) && (jestPlikZr && jestPlikC));
    					}
    					public void changedUpdate(DocumentEvent z) {}
    				});
    	JButton wybierzCel = new JButton("Przegl¹daj");
    	panelCel.add(tekstCel);
    	panelCel.add(wybierzCel);
    	srodekOkna.add(panelCel);
    	wybierzCel.addActionListener(new SluchaczWyboruPlikuC());
    	
    	
    	
    	
    	oknoWyboru = new JFileChooser(".");
    	try
    	{
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
            oknoWyboru.updateUI();
    	}
    	catch (Exception wyj) {}
	}
	
	private class SluchaczWyboruPlikuZr implements ActionListener
	{
		public void actionPerformed(ActionEvent zd)
		{
			
			int wynik = oknoWyboru.showDialog(PanelObslugi.this,"Wybierz");
			if (wynik==JFileChooser.APPROVE_OPTION)
			{
				tekstZrodla.setText(oknoWyboru.getSelectedFile().getPath());
				if (jestSzyfrowanie)
				{
					nazwaPliku = oknoWyboru.getSelectedFile().getName();
					StringTokenizer token = new StringTokenizer(nazwaPliku,".");
					int ile_czlonow = token.countTokens();
					for(int i=1;i<=ile_czlonow;i++)	rozszerzenie = token.nextToken();
					int dl_rozszerzenia = rozszerzenie.length();
					nazwaPliku = oknoWyboru.getSelectedFile().getPath();
					int dl_sciezki = nazwaPliku.length();
					String nazwa = nazwaPliku.substring(0,dl_sciezki-dl_rozszerzenia)+"kod";
					tekstCel.setText(nazwa);
				}
				else tekstCel.setText("");
			}
		}
		
	}
	private class SluchaczWyboruPlikuC implements ActionListener
	{
		public void actionPerformed(ActionEvent zd)
		{
			int wynik = oknoWyboru.showDialog(PanelObslugi.this,"Wybierz");
			if (wynik==JFileChooser.APPROVE_OPTION)
			{
				tekstCel.setText(oknoWyboru.getSelectedFile().getPath());								
			}
		}
		
	}
	private boolean czy_klucz_zapisany= false;
	private JRadioButtonMenuItem opSzyfrowanie;
	private JRadioButtonMenuItem opDeszyfrowanie;
	private String sciezka_klucza;
	private JTextField tekstKlucz;
	private boolean jestPlikZr = false;
	private boolean jestPlikC = false;
	private boolean jestSzyfrowanie = true;
	private String rozszerzenie;
	private JTextField tekstCel;
    private JTextField tekstZrodla;
	private String nazwaPliku;
	private JFileChooser oknoWyboru;
	private JMenuItem gener_klucz;
	private JMenuItem wczyt_klucz_pr,wczyt_klucz_pub,zapisz_klucz_pr,zapisz_klucz_pub;
	private JButton przyciskSzyfrujDeszyfruj;
	private AlgorytmRsa szyfr = new AlgorytmRsa();
}

