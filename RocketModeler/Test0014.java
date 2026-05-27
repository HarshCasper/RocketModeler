//<applet code=Test0014.class width=600 height=350></applet> 


import java.awt.*;
import java.awt.event.*;
import java.applet.*;
import java.text.*;
import java.lang.*;
import java.net.*;
import java.util.*;

class Constants
{
	static double dpicpixpercm = 10;
	static double lpicpixpercm = 1;
	static double lpicpixperm = 25;
	static double lpicmperinc = 5;
	static double lrodlength = 110; //cm
	static double deltat = .045; //s- this is in time of roket launch, not observed speed
	static double cmperinch = 2.54;
	static double pi = Math.PI;
	static double radiansperdegree = (2*pi)/360.0;
	static double bodytubethickness = .1;
	static int rocketfinalheight = (350/2);
	static int identcircwidth = 16;
	static int stagefallrate = 30;  //pixels lower stage falls per iteration after next engine fires
	

	//densities given in g/cm^3, coverted from lb/ft^3 from densities in rocksim
	static double balsadensity = 0.1281;
	static double plasticdensity = 1.049;
	static double hollowplasticdensity = 0.1121;
	static double bodytubedensity = 0.516;

	/*
	Engines
	units = none, mm, mm, g, N, N, s, s, s

	parameters:
	String name;
	double width;
	double length;
	double mass;
	double fuelmass
	double avthrust1;
	double avthrust2;
	double burntime1;
	double totalburntime;
	double delaytime;
	boolean isBooster
	*/
	static Vector engines = new Vector();
	public static void initialize()
	{
		engines.addElement( new Engine("1/2A6-2", 1.3, 4.5, 7.0, 2.0, 6, 0, 0.2, 0.21, 2.0, false) );
		engines.addElement( new Engine("A8-0", 1.8, 7.0, 16.0, 6.0, 10, 0, 0.25, 0.26, 0.0, true) );
		engines.addElement( new Engine("A8-3", 1.8, 7.0, 16.0, 6.0, 10, 0, 0.25, 0.26, 3.0, false) );
		engines.addElement( new Engine("A8-5", 1.8, 7.0, 16.0, 6.0, 10, 0, 0.25, 0.26, 5.0, false) );
		engines.addElement( new Engine("B6-0", 1.8, 7.0, 19.0, 9.0, 25, 6, 0.4, 0.85, 0.0, true) ); 
		engines.addElement( new Engine("B6-2", 1.8, 7.0, 19.0, 9.0, 25, 6, 0.4, 0.85, 2.0, false) ); 
		engines.addElement( new Engine("B6-4", 1.8, 7.0, 19.0, 9.0, 25, 6, 0.4, 0.85, 4.0, false) );
		engines.addElement( new Engine("B6-6", 1.8, 7.0, 19.0, 9.0, 25, 6, 0.4, 0.85, 6.0, false) );
		engines.addElement( new Engine("C6-0", 1.8, 7.0, 25.0, 19.0, 25, 6, 0.4, 1.70, 0.0, true) );
		engines.addElement( new Engine("C6-3", 1.8, 7.0, 25.0, 19.0, 25, 6, 0.4, 1.70, 3.0, false) );
		engines.addElement( new Engine("C6-5", 1.8, 7.0, 25.0, 19.0, 25, 6, 0.4, 1.70, 5.0, false) );
		engines.addElement( new Engine("C6-7", 1.8, 7.0, 25.0, 19.0, 25, 6, 0.4, 1.70, 7.0, false) );
		engines.addElement( new Engine("D12-0", 2.4, 7.0, 45.0, 25.0, 24, 10, 0.6, 1.75, 0.0, true) );
		engines.addElement( new Engine("D12-3", 2.4, 7.0, 45.0, 25.0, 24, 10, 0.6, 1.75, 3.0, false) );
		engines.addElement( new Engine("D12-5", 2.4, 7.0, 45.0, 25.0, 24, 10, 0.6, 1.75, 5.0, false) );
		engines.addElement( new Engine("D12-7", 2.4, 7.0, 45.0, 25.0, 24, 10, 0.6, 1.75, 7.0, false) );
	}
}

class Engine
{
	String name;
	double width;
	double length;
	double mass;
	double fuelmass;
	double avthrust1;
	double avthrust2;
	double burntime1;
	double totalburntime;
	double delaytime;
	boolean isBooster;
	
	Engine(String n, double w, double l, double m, double fm, double at1, double at2, double bt1, double tbt, double dt, boolean ib)
	{
		name = n;
		width = w;
		length = l;
		mass = m;
		fuelmass = fm;
		avthrust1 = at1;
		avthrust2 = at2;
		burntime1 = bt1;
		totalburntime = tbt;
		delaytime = dt;
		isBooster = ib;
	}
}






public class Test0014 extends Applet
{
	
	
	//start defaults
	double bodylength = 33.0;
	double bodywidth = 2.5;
	double noseconelength = 8.0;
	double finlength = 10.0;
	double finwidth = 4.0;
	double finheight = 0.0;
	int finnumber = 4;
	int zoom = 67; 	

	double noseconedensity = (Constants.balsadensity);
	double findensitythickness = (Constants.balsadensity) * (.125 * Constants.cmperinch);
	double recpaymass = 15;
	double parachutediameter = .3;
	double co_o_drag = .7;
	int launchangle = 90;
	int percentrealtime = 500;
	double windvelocity = 0;


	
	Panel designphase, flightphase;
	CardLayout baselayout;
	Button dpbutton, fpbutton;

	int blmod, bwmod, nclmod, flmod, fwmod, fhmod, zmod;
	Scrollbar bladj, bwadj, ncladj, fladj, fwadj, fhadj, zadj;
	Label bllab, bwlab, ncllab, fllab, fwlab, fhlab, zlab, fnlab;
	TextField blfld, bwfld, nclfld, flfld, fwfld, fhfld, zfld;
	Checkbox fourfinbox, threefinbox;


	Label ncmlab, fmlab, ncmcustlab, fmcustlab, e1lab, e2lab, e3lab, rpmasslab, pdlab, cdlab;
	Choice ncmlst, fmlst;
	TextField ncmcustfld, fmcustfld, rpmassfld, cdfld;
	Scrollbar rpmassadj;
	Choice pdlst;
	Choice elst[] = new Choice[3];
	
	Button design, materials;
	TextField cgfld, cpfld, mfld;
	Choice relativelst, outputstgslst;
	Label cglab, cplab, mlab;
	Panel designcardpanel;
	CardLayout designcardlayout;
	RocketCanvas rocketpic;	
	
	Button launchbutton, resetbutton, pasresbutton;
	Scrollbar laadj, speedadj, wvadj;
	Label lalab, speedlab, timelabel, velocitylabel, maxaltlabel, wvlab;
	TextField lafld, speedfld, wvfld;
	LaunchThread launchthrd;


	int numStages = 1;
	Choice stagelst;
	

	public void init()
	{
		Constants.initialize();
		
		setLayout( baselayout = new CardLayout() );
		designphase = new Panel();
		flightphase = new Panel();
		designphase.setBackground(Color.white);
		flightphase.setBackground(Color.white);

		rocketpic = new RocketCanvas	(
						new RocketParams(	numStages, 
									noseconedensity, 
									findensitythickness, 
									new Engine[]
									{
										(Engine)Constants.engines.elementAt(2), 
										null,
										null
									}, 
									recpaymass, 
									bodylength, 
									bodywidth, 
									noseconelength, 
									finlength, 
									finwidth, 
									finheight, 
									finnumber, 
									parachutediameter,
									co_o_drag
								), 
						zoom, 
						numStages,
						true,
						new CgCpMOutput( 	cgfld = new TextField(6), 
									cpfld = new TextField(6),
									mfld = new TextField(6) 
								)  
						);

		
		
		
		//first set up design phase panel, starting with materials panel
		Panel mgrid = new Panel();
		GridBagConstraints gbc = new GridBagConstraints();
		mgrid.setLayout(new GridBagLayout());
		gbc.weightx = 50; 
		gbc.gridx = gbc.gridy = 0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.NORTH;
		gbc.gridwidth = 2;
		
		//ncm
		gbc.weighty = 0;
		ncmlab = new Label("Nose Cone Material:");
		mgrid.add(ncmlab, gbc);
		gbc.weighty = 1;
		gbc.gridy++;
		ncmlst = new Choice();
		ncmlst.addItemListener( new ncmLL());
		ncmlst.add("balsa");
		ncmlst.add("hollow plastic");
		ncmlst.add("custom");
		mgrid.add(ncmlst, gbc);
		gbc.weighty = 0;
		gbc.gridy++;
		ncmcustlab = new Label("Custom Nose Cone Density(g/cubic cm) :");
		ncmcustlab.setForeground(Color.gray);
		mgrid.add(ncmcustlab, gbc);
		gbc.weighty = 1;
		gbc.gridy++;
		ncmcustfld = new TextField(5);
		ncmcustfld.setBackground(Color.lightGray);
		ncmcustfld.setEditable(false);
		ncmcustfld.addActionListener( new ncmcustTL());
		ncmcustfld.setEnabled(false);
		mgrid.add(ncmcustfld, gbc);
		
		gbc.gridy = 0;
		gbc.gridx +=2;

		//fm
		gbc.weighty = 0;
		fmlab = new Label("Fin Material:");
		mgrid.add(fmlab, gbc);
		gbc.weighty = 1;
		gbc.gridy++;
		fmlst = new Choice();
		fmlst.add("1/8\" balsa");
		fmlst.add("1/4\" balsa");
		fmlst.add("1/8\" plastic");
		fmlst.add("1/4\" plastic");
		fmlst.add("1/8\" custom");
		fmlst.add("1/4\" custom");
		fmlst.addItemListener( new fmLL());
		mgrid.add(fmlst, gbc);
		gbc.weighty = 0;
		gbc.gridy++;
		fmcustlab = new Label("Custom Fin Density(g/cubic cm) :");
		fmcustlab.setForeground(Color.gray);
		mgrid.add(fmcustlab, gbc);
		gbc.weighty = 1;
		gbc.gridy++;
		fmcustfld = new TextField(5);
		fmcustfld.setEditable(false);
		fmcustfld.setBackground(Color.lightGray);
		fmcustfld.addActionListener( new fmcustTL());
		fmcustfld.setEnabled(false);
		mgrid.add(fmcustfld, gbc);

		gbc.gridwidth = 1;
		gbc.gridy++;
		gbc.gridx = 0;

		//Engine lists
		for(int stageIndex = 0; stageIndex <3; stageIndex++)
		{
			elst[stageIndex] = new Choice();
			elst[stageIndex].addItemListener(new engineLL(stageIndex+1));
		}
		refreshEngineLists();
		//stage1
		gbc.weighty = 0;
		e1lab = new Label("Stage One Engine:");
		mgrid.add(e1lab, gbc);
		gbc.gridy++;
		mgrid.add(elst[0], gbc);
		gbc.gridy--;
		gbc.gridx++;
		//stage2
		e2lab = new Label("Stage Two Engine:");
		mgrid.add(e2lab, gbc);
		gbc.gridy++;
		mgrid.add(elst[1], gbc);
		gbc.gridy--;
		gbc.gridx++;
		//stage3
		e3lab = new Label("Stage Three Engine:");
		mgrid.add(e3lab, gbc);
		gbc.gridy++;
		mgrid.add(elst[2], gbc);
		
		gbc.gridwidth=4;
		gbc.gridy++;
		gbc.gridx = 0;
		
		//rpmass
		gbc.weighty = 0;
		rpmasslab = new Label("Recovery System and Payload Mass(g) :");
		mgrid.add(rpmasslab, gbc);
		gbc.weighty = 0;
		gbc.gridy++;
		rpmassadj = new Scrollbar(Scrollbar.HORIZONTAL);
		rpmassadj.setMinimum(0);
		rpmassadj.setMaximum(500);
		rpmassadj.setValue((int)recpaymass);
		rpmassadj.addAdjustmentListener(new rpmassAL());
		mgrid.add(rpmassadj, gbc);
		gbc.gridy++;
		gbc.fill = GridBagConstraints.NONE;
		rpmassfld= new TextField(8);
		rpmassfld.setText(Double.toString(recpaymass));
		rpmassfld.addActionListener(new rpmassTL());
		mgrid.add(rpmassfld, gbc);
		gbc.gridy++;



		//pd
		gbc.gridwidth = 2;
		gbc.weighty = 1;
		gbc.anchor = GridBagConstraints.SOUTH;
		pdlab = new Label("Parachute Diameter:");
		mgrid.add(pdlab, gbc);
		gbc.gridy++;
		gbc.weighty = 0;
		gbc.anchor = GridBagConstraints.NORTH;
		pdlst = new Choice();
		pdlst.add("1 foot (30 cm)");
		pdlst.add("2 feet (60 cm)");
		pdlst.add("3 feet (90 cm)");
		pdlst.select("1 foot (30 cm)");
		pdlst.addItemListener(new pdLL());
		mgrid.add(pdlst, gbc);		

		//cd
		gbc.gridy--;
		gbc.gridx++;
		gbc.weighty = 1;
		gbc.anchor = GridBagConstraints.SOUTH;
		cdlab = new Label("Drag Coefficient:");
		mgrid.add(cdlab, gbc);
		gbc.gridy++;
		gbc.weighty = 0;
		gbc.anchor = GridBagConstraints.NORTH;
		cdfld = new TextField(6);
		cdfld.setText(Double.toString(.7));
		cdfld.addActionListener(new cdTL());
		mgrid.add(cdfld, gbc);

		//Then do design phase overall design panel
		Panel dgrid = new Panel();
		gbc = new GridBagConstraints();		
		dgrid.setLayout(new GridBagLayout());
		gbc.weightx = 0;
		gbc.gridx = gbc.gridy = 0;


		//stagelst;
		gbc.weighty = 0;
		gbc.fill = GridBagConstraints.NONE;
		stagelst = new Choice();
		stagelst.add("One Stage Rocket");
		stagelst.add("Two Stage Rocket");
		stagelst.add("Three Stage Rocket");
		stagelst.select("One Stage Rocket");
		numStages=1;
		stagelst.addItemListener(new stageLL());
		dgrid.add(stagelst, gbc);
		gbc.gridy++;
		
		
		//bladj
		gbc.weighty = 0;
		gbc.fill = GridBagConstraints.NONE;
		bllab = new Label("Body Length(cm) :");
		dgrid.add(bllab,gbc);
		gbc.gridy++;
		blfld = new TextField(6);
		blfld.addActionListener(new blTL());
		blfld.setText(Double.toString(bodylength));
		dgrid.add(blfld, gbc);
		gbc.gridy++;
		gbc.weighty = 50;
		gbc.fill = GridBagConstraints.VERTICAL;
		bladj = new Scrollbar(Scrollbar.VERTICAL);
		blmod = setScrollRange(bladj, 100, 1000, bodylength);
		bladj.addAdjustmentListener(new blAL());
		dgrid.add(bladj, gbc);


		//bwadj
		gbc.gridy++;
		gbc.weighty = 0;
		gbc.fill = GridBagConstraints.NONE;
		bwlab = new Label("Body Diameter(cm) :");
		dgrid.add(bwlab,gbc);
		gbc.gridy++;
		bwfld = new TextField(6);
		bwfld.addActionListener(new bwTL());
		bwfld.setText(Double.toString(bodywidth));
		dgrid.add(bwfld, gbc);
		gbc.gridy++;
		gbc.weighty = 50;
		gbc.fill = GridBagConstraints.VERTICAL;
		bwadj = new Scrollbar(Scrollbar.VERTICAL);
		bwmod = setScrollRange(bwadj, 14, 60, bodywidth);
		bwadj.addAdjustmentListener(new bwAL());
		dgrid.add(bwadj, gbc);

		gbc.gridx++;
		gbc.gridy=1;

		//ncladj
		gbc.weighty = 0;
		gbc.fill = GridBagConstraints.NONE;
		ncllab = new Label("Nose Cone Length(cm) :");
		dgrid.add(ncllab,gbc);
		gbc.gridy++;
		nclfld = new TextField(6);
		nclfld.addActionListener(new nclTL());
		nclfld.setText(Double.toString(noseconelength));
		dgrid.add(nclfld, gbc);
		gbc.gridy++;
		gbc.weighty = 50;
		gbc.fill = GridBagConstraints.VERTICAL;
		ncladj = new Scrollbar(Scrollbar.VERTICAL);
		nclmod = setScrollRange(ncladj, 40, 300, noseconelength);
		ncladj.addAdjustmentListener(new nclAL());
		dgrid.add(ncladj, gbc);
		


		//fladj
		gbc.gridy++;
		gbc.weighty = 0;
		gbc.fill = GridBagConstraints.NONE;
		fllab = new Label("Fin Length(cm) :");
		dgrid.add(fllab,gbc);
		gbc.gridy++;
		flfld = new TextField(6);
		flfld.addActionListener(new flTL());
		flfld.setText(Double.toString(finlength));
		dgrid.add(flfld, gbc);
		gbc.gridy++;
		gbc.weighty = 50;
		gbc.fill = GridBagConstraints.VERTICAL;
		fladj = new Scrollbar(Scrollbar.VERTICAL);
		flmod = setScrollRange(fladj, 40, 500, finlength);
		fladj.addAdjustmentListener(new flAL());
		dgrid.add(fladj, gbc);
		

		gbc.gridx++;
		gbc.gridy=1;
	
		//fwadj
		gbc.weighty = 0;
		gbc.fill = GridBagConstraints.NONE;
		fwlab = new Label("Fin Width(cm) :");
		dgrid.add(fwlab,gbc);
		gbc.gridy++;
		fwfld = new TextField(6);
		fwfld.addActionListener(new fwTL());
		fwfld.setText(Double.toString(finwidth));
		dgrid.add(fwfld, gbc);
		gbc.gridy++;
		gbc.weighty = 50;
		gbc.fill = GridBagConstraints.VERTICAL;
		fwadj = new Scrollbar(Scrollbar.VERTICAL);
		fwmod = setScrollRange(fwadj, 20, 200, finwidth);
		fwadj.addAdjustmentListener(new fwAL());
		dgrid.add(fwadj, gbc);

		//fhadj
		gbc.gridy++;
		gbc.weighty = 0;
		gbc.fill = GridBagConstraints.NONE;
		fhlab = new Label("Fin Height(cm) :");
		dgrid.add(fhlab,gbc);
		gbc.gridy++;
		fhfld = new TextField(6);
		fhfld.addActionListener(new fhTL());
		fhfld.setText(Double.toString(finheight));
		dgrid.add(fhfld, gbc);
		gbc.gridy++;
		gbc.weighty = 50;
		gbc.fill = GridBagConstraints.VERTICAL;
		fhadj = new Scrollbar(Scrollbar.VERTICAL);
		fhmod = setScrollRange(fhadj, 0, 500, finheight);
		fhadj.addAdjustmentListener(new fhAL());
		dgrid.add(fhadj, gbc);

		
		gbc.gridx++;
		gbc.gridy=1;
	
		//zadj
		gbc.weighty = 0;
		gbc.fill = GridBagConstraints.NONE;
		zlab = new Label("Zoom(%) :");
		dgrid.add(zlab,gbc);
		gbc.gridy++;
		zfld = new TextField(6);
		zfld.addActionListener(new zTL());
		zfld.setText(Integer.toString(zoom));
		dgrid.add(zfld, gbc);
		gbc.gridy++;
		gbc.weighty = 50;
		gbc.fill = GridBagConstraints.VERTICAL;
		zadj = new Scrollbar(Scrollbar.VERTICAL);
		zmod = setScrollRange(zadj, 10, 300, zoom);
		zadj.setValue(zmod - zoom);
		zadj.addAdjustmentListener(new zAL());
		dgrid.add(zadj, gbc);

		//fin number
		gbc.gridy++;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		fnlab = new Label("Number of Fins:");
		dgrid.add(fnlab, gbc);
		gbc.gridy++;
		fourfinbox = new Checkbox("Four Fins", false);
		dgrid.add(fourfinbox, gbc);
		gbc.gridy++;
		threefinbox = new Checkbox("Three Fins", false);
		dgrid.add(threefinbox, gbc);
		fourfinbox.addItemListener( new fnCL(fourfinbox, threefinbox, 4, 3) );
		threefinbox.addItemListener( new fnCL(threefinbox, fourfinbox, 3, 4) );
		if( finnumber == 3)
			threefinbox.setState(true);
		else
			fourfinbox.setState(true);


		//complete the setup of the design phase panel
		designphase.setLayout(new GridBagLayout());
		
		gbc = new GridBagConstraints();
		
		//phase button
		gbc.anchor = GridBagConstraints.WEST;
		gbc.weighty = 0.0;
		gbc.weightx = 0.01;
		gbc.gridy = gbc.gridx = 0;
		gbc.gridheight = 1;
		gbc.gridwidth = 1;
		fpbutton = new Button("Launch Rocket");
		fpbutton.setBackground(Color.blue);
		fpbutton.setForeground(Color.white);
		fpbutton.addActionListener(new phasebutBL());
		designphase.add(fpbutton, gbc);
		gbc.gridy++;

		//rocketpic
		gbc.anchor = GridBagConstraints.CENTER;
		gbc.weighty = 99.99;
		gbc.weightx = 99.99;
		gbc.gridheight= 5;
		gbc.gridwidth = 2;
		gbc.fill =GridBagConstraints.BOTH;

		


		
		designphase.add(rocketpic, gbc);

		gbc.fill =GridBagConstraints.HORIZONTAL;
		gbc.gridheight = 1;
		gbc.gridwidth = 3;
		gbc.gridy = 0;
		gbc.gridx = gbc.gridx+2;
		gbc.weightx = 0;
		gbc.weighty = 0;	
		design = new Button("Design");
		design.addActionListener( new dbutBL());
		designphase.add(design, gbc);
		
		
		gbc.weightx = 0;
		gbc.gridx = gbc.gridx + 3;
		gbc.gridwidth = 3;
		
		materials = new Button("Materials");
		materials.addActionListener( new mbutBL());
		designphase.add(materials, gbc);
		

		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridx = gbc.gridx - 3;
		gbc.gridy++;
		gbc.weighty = 100;
		gbc.gridwidth = 6;
		designcardpanel = new Panel();
		designcardpanel.setLayout(designcardlayout = new CardLayout());
		designcardpanel.add(dgrid, "design");
		designcardpanel.add(mgrid, "materials");
		designphase.add(designcardpanel, gbc);
		
		
		//output boxes- objects created in rocketpic initialization
		
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.gridwidth = 2;		
		gbc.gridy++;
		gbc.fill = GridBagConstraints.NONE;
		cglab = new Label("Center of Gravity(cm) :");
		designphase.add(cglab, gbc);
		gbc.gridy++;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		cgfld.setEditable(false);
		designphase.add(cgfld, gbc);
		gbc.gridy--;
		gbc.gridx =gbc.gridx + 2;
		gbc.weightx = 0;
		gbc.fill = GridBagConstraints.NONE;
		cplab = new Label("Center of Pressure(cm) :");
		designphase.add(cplab, gbc);
		gbc.gridy++;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		cpfld.setEditable(false);
		designphase.add(cpfld, gbc);
		gbc.gridy--;
		gbc.gridx =gbc.gridx + 2;
		gbc.weightx = 0;
		gbc.fill = GridBagConstraints.NONE;
		mlab = new Label("Rocket Mass(g) :");
		designphase.add(mlab, gbc);
		gbc.gridy++;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		mfld.setEditable(false);
		designphase.add(mfld, gbc);		
		
		
		gbc.gridx=2;
		gbc.gridy++;
		gbc.gridwidth=3;

		
		outputstgslst = new Choice();
		outputstgslst.add("Show CP/CG/Mass For Stage 1                ");
		outputstgslst.addItemListener( new osLL() );
		designphase.add(outputstgslst, gbc);
		gbc.gridx = gbc.gridx+3;
		relativelst = new Choice();
		relativelst.add("CP/CG Values Relative To Bottom of Stage");
		relativelst.add("CP/CG Values Relative To Bottom of Rocket");
		relativelst.select("CP/CG Values Relative To Bottom of Rocket");
		relativelst.addItemListener(new relLL() );
		designphase.add(relativelst, gbc);
		
		
		//add design phase panel to overall layout
		add(designphase, "designphase");


/////////////////////////////////////////////////////////////
		//setup framework for flight phase
		flightphase.setLayout(new GridBagLayout());
		
		
		gbc = new GridBagConstraints();
		//lauchpic
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weighty = gbc.weightx= 100;
		gbc.gridy = gbc.gridx = 0;
		gbc.gridheight = 10;
		gbc.gridwidth = 1;
		try
		{		
			AudioClip thrusters = getAudioClip( new URL(getCodeBase() + "thrusters.au" ));
			AudioClip countdown[] =
			{
				getAudioClip( new URL(getCodeBase() + "1-metallic.au" )),		
				getAudioClip( new URL(getCodeBase() + "2-metallic.au" )),
				getAudioClip( new URL(getCodeBase() + "3-metallic.au" ))
			};
			launchthrd = new LaunchThread(rocketpic.rocket, launchangle, windvelocity, percentrealtime, thrusters, countdown);
			flightphase.add(launchthrd.launchpic, gbc);
			launchthrd.start();	
		}
		catch(MalformedURLException mue){}

		//phase button
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx++;
		gbc.gridheight = 1;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.weighty = 0;
		gbc.weightx = 0;
		dpbutton = new Button("Design Rocket");
		dpbutton.setBackground(Color.blue);
		dpbutton.setForeground(Color.white);
		dpbutton.addActionListener(new phasebutBL());
		flightphase.add(dpbutton, gbc);
		gbc.gridy++;

		//launchbutton;
		launchbutton = new Button("Begin Launch");
		launchbutton.setBackground(Color.red.brighter());
		launchbutton.addActionListener(new launchbutBL());
		flightphase.add(launchbutton, gbc);	
		gbc.gridy++;

		
		//reset button
		resetbutton = new Button("Reset");
		resetbutton.setBackground(Color.white);
		resetbutton.addActionListener(new resetbutBL());
		flightphase.add(resetbutton, gbc);
		gbc.gridy++;

		//pause/resume button(s)
		pasresbutton = new Button("Pause");
		pasresbutton.setBackground(Color.yellow);
		pasresbutton.addActionListener(new pasresbutBL());
		flightphase.add(pasresbutton, gbc);
		gbc.gridy++;
		

		//la
		gbc.fill = GridBagConstraints.NONE;
		gbc.anchor = GridBagConstraints.CENTER;
		lalab = new Label("Launch Angle(degees):");
		flightphase.add(lalab, gbc);
		gbc.gridy++;
		lafld = new TextField(6);
		lafld.addActionListener( new laTL() );
		lafld.setText(Double.toString(launchangle));
		flightphase.add(lafld, gbc);
		gbc.gridy++;
		gbc.fill = GridBagConstraints.VERTICAL;
		gbc.weighty = 30;
		laadj = new Scrollbar(Scrollbar.VERTICAL);
		laadj.setMaximum(90 + laadj.getBlockIncrement());
		laadj.setMinimum(60);
		laadj.setValue(launchangle);
		laadj.addAdjustmentListener(new laAL());
		flightphase.add(laadj, gbc);
		gbc.gridy++;
		

		//speed
		gbc.fill = GridBagConstraints.NONE;
		gbc.weighty = 0;
		speedlab = new Label("Percentage Real Time:");
		flightphase.add(speedlab, gbc);
		gbc.gridy++;
		speedfld = new TextField(6);
		speedfld.addActionListener( new speedTL() );
		speedfld.setText(Double.toString(percentrealtime));
		flightphase.add(speedfld, gbc);
		gbc.gridy++;
		gbc.fill = GridBagConstraints.VERTICAL;
		gbc.weighty = 30;
		speedadj = new Scrollbar(Scrollbar.VERTICAL);
		speedadj.setUnitIncrement(25);
		speedadj.setMaximum(1000 + speedadj.getBlockIncrement());
		speedadj.setMinimum(100);
		speedadj.setValue(percentrealtime);
		speedadj.addAdjustmentListener(new speedAL());
		flightphase.add(speedadj, gbc);


		//time field
		gbc.gridy = 0;
		gbc.weighty = 0;
		gbc.gridx++;
		gbc.anchor = GridBagConstraints.CENTER;
		gbc.fill = GridBagConstraints.NONE;	
		timelabel = new Label("Time Elapsed(s):");
		flightphase.add(timelabel, gbc);
		gbc.gridy++;
		flightphase.add(launchthrd.timefld, gbc);
		gbc.gridy++;		


		//velocity field
		velocitylabel = new Label("Current Velocity(m/s):");
		flightphase.add(velocitylabel, gbc);
		gbc.gridy++;
		flightphase.add(launchthrd.velocityfld, gbc);
		gbc.gridy++;


		//max altitude field
		maxaltlabel = new Label("Max Altitude Attained(m):");
		flightphase.add(maxaltlabel, gbc);
		gbc.gridy++;
		flightphase.add(launchthrd.maxaltfld, gbc);
		gbc.gridy++;
		gbc.gridy++;
		

		//wv	
		gbc.fill = GridBagConstraints.NONE;
		gbc.weighty = 0;	
		wvlab = new Label("Wind Speed(mph):");
		flightphase.add(wvlab, gbc);
		gbc.gridy++;
		wvfld = new TextField(6);
		wvfld.addActionListener( new wvTL() );
		wvfld.setText(Double.toString(windvelocity));
		flightphase.add(wvfld, gbc);
		gbc.gridy++;
		gbc.fill = GridBagConstraints.VERTICAL;
		gbc.weighty = 30;
		wvadj = new Scrollbar(Scrollbar.VERTICAL);
		wvadj.setMaximum(500 + wvadj.getBlockIncrement());
		wvadj.setMinimum(-500);
		wvadj.setValue((int)(windvelocity * -10));
		wvadj.addAdjustmentListener(new wvAL());
		flightphase.add(wvadj, gbc);
		
					
		add(flightphase, "flightphase");

	}
	
	//sets scrollbar range and default and returns modifier value
	//because max is set as bottom value and I want max to be at top
	//this returns a modifier so modifier - bar.getValue() = value I want
	public int setScrollRange(Scrollbar bar, int min, int max, double startvalue)
	{
		int modifier = max + min;
		bar.setMaximum(max + bar.getBlockIncrement());
		bar.setMinimum(min);
		bar.setValue(modifier - (int)(Constants.dpicpixpercm * startvalue));
		return(modifier);
	}


	
	public double roundtotenths(double roundee)
	{
		roundee = ((double)(Math.round(roundee * 10.0)))/10.0;
		return(roundee);
	}

	
	class phasebutBL implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			launchthrd.pause = true;
			launchthrd.launchpic.initialize();
			launchthrd.launchpic.repaint();
			pasresbutton.setBackground( Color.yellow );
			pasresbutton.setForeground( Color.black );
			pasresbutton.setLabel("Pause");
			baselayout.next(designphase.getParent());
		}
	}

	class launchbutBL implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			launchthrd.pause = true;
			pasresbutton.setBackground( Color.yellow );
			pasresbutton.setForeground( Color.black );
			pasresbutton.setLabel("Pause");
			try
			{
				Thread.currentThread().sleep(1000);
			}
			catch(InterruptedException ie){}
			launchthrd.launchpic.initialize();
			launchthrd.timefld.setText(Double.toString(  launchthrd.launchpic.timeelapsed  ));
			launchthrd.velocityfld.setText(Double.toString( launchthrd.launchpic.lastyvelocity ));
			launchthrd.maxaltfld.setText(Double.toString( launchthrd.launchpic.maxaltitude ));
			launchthrd.launchpic.repaint();
			launchthrd.launchpic.launchphase = 1;
			launchthrd.pause = false;
		}
	}
	class resetbutBL implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			launchthrd.pause = true;
			pasresbutton.setBackground( Color.yellow );
			pasresbutton.setForeground( Color.black );
			pasresbutton.setLabel("Pause");
			launchthrd.launchpic.initialize();
			launchthrd.timefld.setText(Double.toString(  launchthrd.launchpic.timeelapsed  ));
			launchthrd.velocityfld.setText(Double.toString( launchthrd.launchpic.lastyvelocity ));
			launchthrd.maxaltfld.setText(Double.toString( launchthrd.launchpic.maxaltitude ));
			launchthrd.launchpic.repaint();
		}
	}

	class pasresbutBL implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			
			if( !((launchthrd.pause == true || launchthrd.launchpic.launchphase ==0)&& pasresbutton.getBackground() == Color.yellow) )
			{
				if(launchthrd.pause == false)
				{
					launchthrd.pause = true;
					pasresbutton.setBackground( Color.magenta.darker().darker() );
					pasresbutton.setForeground( Color.white );
					pasresbutton.setLabel("Resume");
				}
				else
				{
					launchthrd.pause = false;
					pasresbutton.setBackground( Color.yellow );
					pasresbutton.setForeground( Color.black );
					pasresbutton.setLabel("Pause");
				}

			}
			repaint();
		}
	}
	

	//stagelst listener
	class stageLL implements ItemListener
	{
		public void itemStateChanged(ItemEvent e)
		{

			double changeInRocketSize = 0;
			int changeInStages = 0;
			int oldNumStages = rocketpic.rocket.getNumStages();
			if( stagelst.getSelectedItem() == "One Stage Rocket")
			{
				changeInStages = 1 - oldNumStages;
				changeInRocketSize = rocketpic.rocket.setNumStages(1);			
			}
			if( stagelst.getSelectedItem() == "Two Stage Rocket")
			{
				changeInStages = 2 - oldNumStages;
				changeInRocketSize = rocketpic.rocket.setNumStages(2);			
			}
			if( stagelst.getSelectedItem() == "Three Stage Rocket")
			{
				changeInStages = 3 - oldNumStages;
				changeInRocketSize = rocketpic.rocket.setNumStages(3);		
			}
			
			//update bodylength and finlength controls to reflect addition	
			int blmin = bladj.getMinimum() + (int)(Constants.dpicpixpercm * changeInRocketSize);
			int blmax = bladj.getMaximum() + (int)(Constants.dpicpixpercm * changeInRocketSize) - bladj.getBlockIncrement();
			double blvalue = ((double)(blmod - bladj.getValue())/Constants.dpicpixpercm) + changeInRocketSize;
			blmod = setScrollRange(bladj, blmin, blmax, blvalue);
			blfld.setText(Double.toString((double)(blmod - bladj.getValue())/Constants.dpicpixpercm));
			
			int flmin = fladj.getMinimum() + (int)(Constants.dpicpixpercm * changeInRocketSize);
			int flmax = fladj.getMaximum() + (int)(Constants.dpicpixpercm * changeInRocketSize) - fladj.getBlockIncrement();
			double flvalue = ((double)(flmod - fladj.getValue())/Constants.dpicpixpercm) + changeInRocketSize;
			flmod = setScrollRange(fladj, flmin, flmax, flvalue);
			flfld.setText(Double.toString((double)(flmod - fladj.getValue())/Constants.dpicpixpercm));
			
			
			
			//outputstgslst.add("Show CP/CG/Mass For Stage 1");
			
			//remove all data from outputstgslst
			while( outputstgslst.getItemCount() > 0)
			{
				outputstgslst.remove(0);
			}
			for(int stageIndex = 0; stageIndex < rocketpic.rocket.getNumStages(); stageIndex++)
			{
				String option = "Show CP/CG/Mass For Stage";
				if( stageIndex == rocketpic.rocket.getNumStages()-1)
				{
					option = option + " ";
				}
				else
				{
					option = option + "s ";
				}
				for(int stageCount = stageIndex + 1; stageCount <= rocketpic.rocket.getNumStages(); stageCount++)
				{
					if(stageCount > stageIndex+1 && stageCount != rocketpic.rocket.getNumStages())
					{
						option = option + ",";
					}
					if(stageCount > stageIndex+1 && stageCount == rocketpic.rocket.getNumStages())
					{
						option = option + "&";
					}
					option = option + stageCount;
				}
				outputstgslst.add(option);
			}
			outputstgslst.select(0);			
			
			refreshEngineLists();
			rocketpic.repaint();

		}
	}

	public void refreshEngineLists()
	{
		for (int stageIndex = 0; stageIndex < 3; stageIndex++)
		{
			while(elst[stageIndex].getItemCount() > 0)
			{	
				elst[stageIndex].remove(0);
			}
		}
		
		for(int stageIndex = 0; stageIndex < rocketpic.rocket.numStages; stageIndex++)
		{
			boolean isBoosterStage = (stageIndex < rocketpic.rocket.numStages-1);
			elst[stageIndex].setEnabled(true);
			for(int eIndex = 0; eIndex< Constants.engines.size(); eIndex++)
			{
				Engine nextEngine = (Engine)Constants.engines.elementAt(eIndex);
				if( 
					(!nextEngine.isBooster && !isBoosterStage) 
					|| (nextEngine.isBooster && isBoosterStage)
				)
				{
					elst[stageIndex].add(nextEngine.name);
				}
			}
			if(rocketpic != null)
			{
				updateEngineList(elst[stageIndex], isBoosterStage);
			}
			elst[stageIndex].select( (rocketpic.rocket.getEngine(stageIndex+1)).name);

		}
		for(int stageIndex = rocketpic.rocket.numStages; stageIndex < 3; stageIndex++)
		{
			elst[stageIndex].setEnabled(false);
		}
	}

	class osLL implements ItemListener
	{
		public void itemStateChanged(ItemEvent e)
		{
			String selected = outputstgslst.getSelectedItem();
			int stagesShowing = 0;
			if(selected.indexOf("1") > 0)
			{
				stagesShowing++;
			}
			if(selected.indexOf("2") > 0)
			{
				stagesShowing++;
			}
			if(selected.indexOf("3") > 0)
			{
				stagesShowing++;
			}
			rocketpic.setNumStagesShowing(stagesShowing);
		}
	}
	class relLL implements ItemListener
	{
		public void itemStateChanged(ItemEvent e)
		{
			//relativelst.add("CP/CG Values Relative To Bottom of Stage");
			//relativelst.add("CP/CG Values Relative To Bottom of Rocket");

			
			String selected = relativelst.getSelectedItem();
			if(selected == "CP/CG Values Relative To Bottom of Stage")
			{
				rocketpic.displayValuesFromBottomOfRocket=false;
			}
			else
			{
				rocketpic.displayValuesFromBottomOfRocket=true;
			}
			rocketpic.repaint();
		}	
	}
	
	


	class laAL implements AdjustmentListener
	{
		public void adjustmentValueChanged(AdjustmentEvent e)
		{
			if(launchthrd.launchpic.launchphase == 0)
			{
				launchthrd.launchpic.launchangle = laadj.getValue();
				lafld.setText(Double.toString( laadj.getValue() ));
				launchthrd.launchpic.initialize();
				launchthrd.launchpic.repaint();
			}
			else
				laadj.setValue(launchthrd.launchpic.launchangle);
			
		}
	}
	class laTL implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			if(launchthrd.launchpic.launchphase == 0)
			{
				try
				{
					Double laD = Double.valueOf(lafld.getText());
					double la = laD.doubleValue();
					if( la < laadj.getMinimum() || la > laadj.getMaximum() - laadj.getBlockIncrement() )
					{
						if(la < laadj.getMinimum() )
							la = laadj.getMinimum();
						if(la > laadj.getMaximum() - laadj.getBlockIncrement() )
							la = laadj.getMaximum() - laadj.getBlockIncrement();
						lafld.setText(Double.toString(la));
					}
					laadj.setValue( (int)la );
					launchthrd.launchpic.launchangle = (int)la;
					launchthrd.launchpic.initialize();
				}
				catch(NumberFormatException x)
				{
					lafld.setText(Double.toString( laadj.getValue() ));
					return;
				}
			}
			else
				lafld.setText(Double.toString( launchthrd.launchpic.launchangle ));
					
		}
	}
	
	class speedAL implements AdjustmentListener
	{
		public void adjustmentValueChanged(AdjustmentEvent e)
		{
			launchthrd.percentrealtime = speedadj.getValue();
			speedfld.setText(Double.toString( speedadj.getValue() ));
		}
	}
	class speedTL implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			try
			{
				Double speedD = Double.valueOf(speedfld.getText());
				double speed = speedD.doubleValue();
				if( speed < speedadj.getMinimum() || speed > speedadj.getMaximum() - speedadj.getBlockIncrement() )
				{
					if(speed < speedadj.getMinimum() )
						speed = speedadj.getMinimum();
					if(speed > speedadj.getMaximum() - speedadj.getBlockIncrement() )
						speed = speedadj.getMaximum() - speedadj.getBlockIncrement();
					speedfld.setText(Double.toString(speed));
				}
				speedadj.setValue( (int)speed );
				launchthrd.percentrealtime = (int)speed;
			}
			catch(NumberFormatException x)
			{
				speedfld.setText(Double.toString( speedadj.getValue() ));
				return;
			}		
		}
	}


	class wvAL implements AdjustmentListener
	{
		public void adjustmentValueChanged(AdjustmentEvent e)
		{
			if(launchthrd.launchpic.launchphase == 0)
			{
				launchthrd.launchpic.windvelocity = wvadj.getValue()/-10.0;
				wvfld.setText(Double.toString( wvadj.getValue()/-10.0 ));
				launchthrd.launchpic.initialize();
			}
			else
				wvadj.setValue( (int)(launchthrd.launchpic.windvelocity * -10));
		}
	}
	class wvTL implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			if(launchthrd.launchpic.launchphase == 0)
			{
				try
				{
					Double wvD = Double.valueOf(wvfld.getText());
					double wv = wvD.doubleValue() * -10;
					if( wv < wvadj.getMinimum() || wv > wvadj.getMaximum() - wvadj.getBlockIncrement() )
					{
						if(wv < wvadj.getMinimum() )
							wv = wvadj.getMinimum();
						if(wv > wvadj.getMaximum() - wvadj.getBlockIncrement() )
							wv = wvadj.getMaximum() - wvadj.getBlockIncrement();
						wvfld.setText(Double.toString(wv/-10.0));
					}
					wvadj.setValue( (int)wv  );
					launchthrd.launchpic.windvelocity = wv/-10.0;
					launchthrd.launchpic.initialize();
				}
				catch(NumberFormatException x)
				{
					wvfld.setText(Double.toString( wvadj.getValue()/-10.0 ));
					return;
				}
			}
			else
			{
				wvfld.setText(Double.toString( (int)launchthrd.launchpic.windvelocity ));
			}			
		}
	}



	class dbutBL implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			designcardlayout.show(designcardpanel, "design");
		}
	}

	class mbutBL implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			designcardlayout.show(designcardpanel, "materials");
		}
	}	
	
	//ncm listeners
	class ncmLL implements ItemListener
	{
		public void itemStateChanged(ItemEvent e)
		{

			if( ncmlst.getSelectedItem() == "balsa")
			{
				rocketpic.rocket.noseconedensity = Constants.balsadensity;			
			}
			if( ncmlst.getSelectedItem() == "hollow plastic")
			{
				rocketpic.rocket.noseconedensity = Constants.hollowplasticdensity;
			}
			if( ncmlst.getSelectedItem() == "custom")
			{
				ncmcustlab.setForeground(Color.black);
				ncmcustfld.setBackground(null);
				ncmcustfld.setEditable(true);
				ncmcustfld.setEnabled(true);
				
			}
			else
			{
				ncmcustlab.setForeground(Color.gray);
				ncmcustfld.setBackground(Color.lightGray);
				ncmcustfld.setEditable(false);
				ncmcustfld.setText("");
				ncmcustfld.setEnabled(false);
			}
			rocketpic.repaint();

		}
	}
	class ncmcustTL implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			try
			{
				Double custdensity = Double.valueOf(ncmcustfld.getText());
				
				if(custdensity.doubleValue() < .001 )
				{
					custdensity = Double.valueOf("0.001");
					ncmcustfld.setText("0.001");					
				}
				if(custdensity.doubleValue() > 100.0 )
				{
					custdensity = Double.valueOf("100.0");
					ncmcustfld.setText("100.0");					
				}
				
				rocketpic.rocket.noseconedensity = custdensity.doubleValue();

				rocketpic.repaint();

			}
			catch(NumberFormatException x)
			{ 
				ncmlst.select("balsa");
				rocketpic.rocket.noseconedensity = Constants.balsadensity;
				ncmcustlab.setForeground(Color.gray);
				ncmcustfld.setBackground(Color.lightGray);
				ncmcustfld.setEditable(false);
				ncmcustfld.setEnabled(false);
				ncmcustfld.setText("");	
				rocketpic.repaint();
				return;
			}
		}
	}
	

	//fm listeners
	class fmLL implements ItemListener
	{
		public void itemStateChanged(ItemEvent e)
		{
			if( fmlst.getSelectedItem() == "1/8\" balsa")
			{
				rocketpic.rocket.findensitythickness = (Constants.balsadensity) * (.125 *Constants.cmperinch);
			}
			if( fmlst.getSelectedItem() == "1/4\" balsa")
			{
				rocketpic.rocket.findensitythickness = (Constants.balsadensity) * (.25 *Constants.cmperinch);
			}
			if( fmlst.getSelectedItem() == "1/8\" plastic")
			{
				rocketpic.rocket.findensitythickness = (Constants.plasticdensity) * (.125 *Constants.cmperinch);

			}
			if( fmlst.getSelectedItem() == "1/4\" plastic")
			{
				rocketpic.rocket.findensitythickness = (Constants.plasticdensity) * (.25 *Constants.cmperinch);

			}

			if( fmlst.getSelectedItem() == "1/8\" custom" || fmlst.getSelectedItem() == "1/4\" custom")
			{
				fmcustlab.setForeground(Color.black);
				fmcustfld.setBackground(null);
				fmcustfld.setEditable(true);
				fmcustfld.setEnabled(true);
			}
			else
			{
				fmcustlab.setForeground(Color.gray);
				fmcustfld.setBackground(Color.lightGray);
				fmcustfld.setEditable(false);
				fmcustfld.setText("");
				fmcustfld.setEnabled(false);
			}
			rocketpic.repaint();
		}
	}
	class fmcustTL implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			try
			{
				Double custdensity = Double.valueOf(fmcustfld.getText());
						

				if(custdensity.doubleValue() < .001 )
				{
					custdensity = Double.valueOf("0.001");
					fmcustfld.setText("0.001");					
				}
				if(custdensity.doubleValue() > 100.0 )
				{
					custdensity = Double.valueOf("100.0");
					fmcustfld.setText("100.0");					
				}
		

				if(fmlst.getSelectedItem() == "1/8\" custom")
				{
					rocketpic.rocket.findensitythickness = (custdensity.doubleValue()) * (.125 *Constants.cmperinch);
				}
				if(fmlst.getSelectedItem() == "1/4\" custom")
				{
					rocketpic.rocket.findensitythickness = (custdensity.doubleValue()) * (.25 *Constants.cmperinch);	
				}
				rocketpic.repaint();
			}
			catch(NumberFormatException x)
			{ 
				fmlst.select("1/8\" balsa");
				rocketpic.rocket.findensitythickness = (Constants.balsadensity) * (.125 *Constants.cmperinch);
				fmcustlab.setForeground(Color.gray);
				fmcustfld.setBackground(Color.lightGray);
				fmcustfld.setEditable(false);
				fmcustfld.setEnabled(false);
				fmcustfld.setText("");	
				rocketpic.repaint();
				return;
			}
		}
	}
	
	//engine listener
	class engineLL implements ItemListener
	{
		int stageNumber;
		public engineLL(int stnum)
		{
			stageNumber = stnum;	
		}
		public void itemStateChanged(ItemEvent e)
		{
			String selection = elst[stageNumber-1].getSelectedItem();
			boolean found = false;
			for(int eIndex = 0; eIndex < Constants.engines.size() && !found; eIndex++)
			{
				Engine testEngine = (Engine)Constants.engines.elementAt(eIndex);
				if(testEngine.name == selection)
				{
					rocketpic.rocket.setEngine(stageNumber, testEngine);
					found = true;
				}
			}
			
			rocketpic.repaint();	
		}
	}

	//rpmass listeners
	class rpmassAL implements AdjustmentListener
	{
		public void adjustmentValueChanged(AdjustmentEvent e)
		{
			rocketpic.rocket.recpaymass = rpmassadj.getValue();
			rpmassfld.setText(Double.toString( (double)rpmassadj.getValue() ));
			rocketpic.repaint();
		}
	}
	class rpmassTL implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			try
			{
				Double rpmassD = Double.valueOf(rpmassfld.getText());
				double rpmass = rpmassD.doubleValue();
				if( rpmass < (double)(rpmassadj.getMinimum()) || rpmass > (double)(rpmassadj.getMaximum()) - rpmassadj.getBlockIncrement() )
				{
					if(rpmass < (double)(rpmassadj.getMinimum())  )
						rpmass = (double)(rpmassadj.getMinimum());
					if(rpmass >  (double)(rpmassadj.getMaximum()) - rpmassadj.getBlockIncrement()  )
						rpmass =  (double)(rpmassadj.getMaximum()) - rpmassadj.getBlockIncrement();
					rpmassfld.setText(Double.toString(rpmass));	
				}
				rpmassadj.setValue((int)(rpmass));
				rocketpic.rocket.recpaymass = rpmass;
				rocketpic.repaint();				
			}
			catch(NumberFormatException x)
			{
				rpmassfld.setText(Double.toString(rpmassadj.getValue())); 
				return;
			}
		}
	}
	
	
	class pdLL implements ItemListener
	{
		public void itemStateChanged(ItemEvent e)
		{
			if( pdlst.getSelectedItem() == "1 foot (30 cm)")
			{
				rocketpic.rocket.parachutediameter = .3;
			}
			if( pdlst.getSelectedItem() == "2 feet (60 cm)")
			{
				rocketpic.rocket.parachutediameter = .6;
			}
			if( pdlst.getSelectedItem() == "3 feet (90 cm)")
			{
				rocketpic.rocket.parachutediameter = .9;

			}
		}
	}

	class cdTL implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			
			try
			{
				Double cdD = Double.valueOf(cdfld.getText());
				double cd = cdD.doubleValue();
				if( cd < 0.0 || cd > 2.0 )
				{
					if(cd < 0.0 )
						cd = 0.0;
					if(cd > 2.0 )
						cd = 2.0;
					cdfld.setText(Double.toString(cd));
				}
				rocketpic.rocket.co_o_drag = cd;
			}
			catch(NumberFormatException x)
			{
				cdfld.setText(Double.toString(rocketpic.rocket.co_o_drag));
			}
		}
	}



	//bl listeners
	class blAL implements AdjustmentListener
	{
		public void adjustmentValueChanged(AdjustmentEvent e)
		{
			if((blmod-bladj.getValue())/Constants.dpicpixpercm > rocketpic.rocket.getFinHeight()+rocketpic.rocket.getFinLength())
			{
				rocketpic.rocket.setBodyLength( (double)(blmod - bladj.getValue())/Constants.dpicpixpercm);
				blfld.setText(Double.toString((double)(blmod - bladj.getValue())/Constants.dpicpixpercm));
			}
			else
			{
				bladj.setValue( (int)(blmod - Constants.dpicpixpercm * (rocketpic.rocket.getFinHeight() + rocketpic.rocket.getFinLength())) );
				blfld.setText(Double.toString(roundtotenths(rocketpic.rocket.getFinHeight() + rocketpic.rocket.getFinLength())));
				rocketpic.rocket.setBodyLength( rocketpic.rocket.getFinHeight() + rocketpic.rocket.getFinLength() );
			}
			rocketpic.repaint();
		}
	}
	class blTL implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			try
			{
				Double blD = Double.valueOf(blfld.getText());
				double bl = blD.doubleValue();
				if( 	bl < rocketpic.rocket.getFinHeight() + rocketpic.rocket.getFinLength() 
					|| bl < (bladj.getMinimum()/Constants.dpicpixpercm) 
					|| bl > ((bladj.getMaximum() - bladj.getBlockIncrement())/Constants.dpicpixpercm) 
				)
				{
					if(bl < (bladj.getMinimum()/Constants.dpicpixpercm))
						bl = (bladj.getMinimum()/Constants.dpicpixpercm);
					if(bl > ((bladj.getMaximum() - bladj.getBlockIncrement())/Constants.dpicpixpercm) )
						bl = bladj.getMaximum() - bladj.getBlockIncrement();
					if(bl < rocketpic.rocket.getFinHeight() + rocketpic.rocket.getFinLength())
						bl = rocketpic.rocket.getFinHeight() + rocketpic.rocket.getFinLength();
					blfld.setText(Double.toString(bl));
					
				}
				bladj.setValue( (int)(blmod - (bl * Constants.dpicpixpercm)) );

				rocketpic.rocket.setBodyLength(bl);
				rocketpic.repaint();		
			}
			catch(NumberFormatException x)
			{
				blfld.setText(Double.toString((double)(blmod - bladj.getValue())/(Constants.dpicpixpercm))); 
				return;
			}
		}
	}

	//bw listeners
	class bwAL implements AdjustmentListener
	{
		public void adjustmentValueChanged(AdjustmentEvent e)
		{
			if(bwmod - bwadj.getValue() >= Constants.dpicpixpercm * (rocketpic.rocket.largestEngineWidth + .1))
			{
				rocketpic.rocket.bodywidth = ((double)(bwmod - bwadj.getValue()))/Constants.dpicpixpercm;
				bwfld.setText(Double.toString(((double)(bwmod - bwadj.getValue()))/Constants.dpicpixpercm));

			}
			else
			{
				bwadj.setValue( (int)(bwmod - Constants.dpicpixpercm * (rocketpic.rocket.largestEngineWidth + .1)) );
				bwfld.setText(Double.toString(roundtotenths( (rocketpic.rocket.largestEngineWidth + .1) ) ) );
				rocketpic.rocket.bodywidth = rocketpic.rocket.largestEngineWidth + .1;
			}

				
			updateEngineList(elst[0], false);

			rocketpic.repaint();
		}
	}
	class bwTL implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			try
			{
				Double bwD = Double.valueOf(bwfld.getText());
				double bw = bwD.doubleValue();
				if( 	bw < rocketpic.rocket.largestEngineWidth + .1 
					|| bw < (bwadj.getMinimum()/Constants.dpicpixpercm) 
					|| bw > ((bwadj.getMaximum() - bwadj.getBlockIncrement())/Constants.dpicpixpercm) 
				)
				{
					if(bw < (bwadj.getMinimum()/Constants.dpicpixpercm))
						bw = (bwadj.getMinimum()/Constants.dpicpixpercm);
					if(bw > ((bwadj.getMaximum() - bwadj.getBlockIncrement())/Constants.dpicpixpercm) )
						bw = bwadj.getMaximum() - bwadj.getBlockIncrement();
					if(bw < rocketpic.rocket.largestEngineWidth + .1)
						bw = rocketpic.rocket.largestEngineWidth + .1;
					bwfld.setText(Double.toString(bw));	
				}
				
				bwadj.setValue( (int)(bwmod - (bw * Constants.dpicpixpercm)) );
				rocketpic.rocket.bodywidth = bw;

				updateEngineList(elst[0], false);
				
				rocketpic.repaint();			
			}
			catch(NumberFormatException x)
			{
				bwfld.setText(Double.toString((double)(bwmod - bwadj.getValue())/(Constants.dpicpixpercm))); 
				return;
			}
		}
	}
	public void updateEngineList( Choice engineList, boolean isBoosterList)
	{
		for(int elIndex =0; elIndex < engineList.getItemCount(); elIndex++)
		{
			boolean found = false;
			double engineWidth = 0;
			for(int eIndex =0; eIndex < Constants.engines.size() && !found; eIndex++)
			{
				if( engineList.getItem(elIndex) == ((Engine)Constants.engines.elementAt(eIndex)).name )
				{
					found = true;
					engineWidth = ((Engine)Constants.engines.elementAt(eIndex)).width;
				}
				if(found && rocketpic.rocket.bodywidth < engineWidth + .1)
				{
					engineList.remove( elIndex );
					elIndex--;
				}
			}
		}
		for(int eIndex = 0; eIndex < Constants.engines.size(); eIndex++)
		{
			Engine testEngine= (Engine)Constants.engines.elementAt(eIndex);
			if( (isBoosterList && !testEngine.isBooster) || (!isBoosterList && testEngine.isBooster) )
			{
				continue;
			}
			boolean found = false;
			double engineWidth = testEngine.width;
			String engineName = testEngine.name;
			for(int elIndex = 0; elIndex < engineList.getItemCount() && !found; elIndex++)
			{
				if(engineList.getItem(elIndex) == engineName)
				{
					found = true;
				}
			}
			if(!found && rocketpic.rocket.bodywidth >= engineWidth + .1)
			{
				engineList.add(engineName);
			}
		}
	}





	

	//ncl listeners
	class nclAL implements AdjustmentListener
	{
		public void adjustmentValueChanged(AdjustmentEvent e)
		{
			rocketpic.rocket.noseconelength = ((double)(nclmod - ncladj.getValue()))/Constants.dpicpixpercm;
			nclfld.setText(Double.toString((double)(nclmod - ncladj.getValue())/Constants.dpicpixpercm));
			rocketpic.repaint();
		}
	}
	class nclTL implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			try
			{
				Double nclD = Double.valueOf(nclfld.getText());
				double ncl = nclD.doubleValue();
				if( ncl < (ncladj.getMinimum()/Constants.dpicpixpercm) || ncl > ((ncladj.getMaximum() - ncladj.getBlockIncrement())/Constants.dpicpixpercm) )
				{
					if(ncl < (ncladj.getMinimum()/Constants.dpicpixpercm))
						ncl = (ncladj.getMinimum()/Constants.dpicpixpercm);
					if(ncl > ((ncladj.getMaximum() - ncladj.getBlockIncrement())/Constants.dpicpixpercm) )
						ncl = ncladj.getMaximum() - ncladj.getBlockIncrement();
					nclfld.setText(Double.toString(ncl));
					
				}
				ncladj.setValue( (int)(fwmod - (ncl * Constants.dpicpixpercm)) );

				rocketpic.rocket.noseconelength = ncl;
				rocketpic.repaint();			
			}
			catch(NumberFormatException x)
			{
				nclfld.setText(Double.toString((double)(nclmod - ncladj.getValue())/(Constants.dpicpixpercm))); 
				return;
			}
		}
	}

	//fl listeners
	class flAL implements AdjustmentListener
	{
		public void adjustmentValueChanged(AdjustmentEvent e)
		{
			if(rocketpic.rocket.getBodyLength() > rocketpic.rocket.getFinHeight() + ((double)(flmod - fladj.getValue()))/Constants.dpicpixpercm )
			{
				rocketpic.rocket.setFinLength((double)((flmod - fladj.getValue())/(Constants.dpicpixpercm)));
				flfld.setText(Double.toString((double)((flmod - fladj.getValue())/(Constants.dpicpixpercm))));
			}
			else
			{
				fladj.setValue( flmod - (int)((rocketpic.rocket.getBodyLength() - rocketpic.rocket.getFinHeight())*(Constants.dpicpixpercm)) );
				flfld.setText(Double.toString(roundtotenths(rocketpic.rocket.getBodyLength() - rocketpic.rocket.getFinHeight())));
				rocketpic.rocket.setFinLength(rocketpic.rocket.getBodyLength() - rocketpic.rocket.getFinHeight());
			}
			rocketpic.repaint();
		}
	}
	class flTL implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			try
			{
				Double flD = Double.valueOf(flfld.getText());
				double fl = flD.doubleValue();
				if( 	fl > rocketpic.rocket.getBodyLength() - rocketpic.rocket.getFinHeight()  
					|| fl < (fladj.getMinimum()/Constants.dpicpixpercm) 
					|| fl > ((fladj.getMaximum() - fladj.getBlockIncrement())/Constants.dpicpixpercm) 
				)
				{
					if(fl < (fladj.getMinimum()/Constants.dpicpixpercm) )
						fl = (fladj.getMinimum()/Constants.dpicpixpercm);
					if(fl > ((fladj.getMaximum() - fladj.getBlockIncrement())/Constants.dpicpixpercm) )
						fl = ((fladj.getMaximum() - fladj.getBlockIncrement())/Constants.dpicpixpercm);
					if(fl > rocketpic.rocket.getBodyLength() - rocketpic.rocket.getFinHeight())
						fl = rocketpic.rocket.getBodyLength() - rocketpic.rocket.getFinHeight();
					flfld.setText(Double.toString(fl));
					
				}
				fladj.setValue( (int)(flmod - (fl * Constants.dpicpixpercm)) );
				rocketpic.rocket.setFinLength(fl);
				rocketpic.repaint();			
			}
			catch(NumberFormatException x)
			{
				flfld.setText(Double.toString((double)(flmod - fladj.getValue())/(Constants.dpicpixpercm))); 
				return;
			}
		}
	}


	//fw listeners
	class fwAL implements AdjustmentListener
	{
		public void adjustmentValueChanged(AdjustmentEvent e)
		{
			rocketpic.rocket.setFinWidth( (fwmod - fwadj.getValue())/Constants.dpicpixpercm );
			fwfld.setText(Double.toString((double)(fwmod - fwadj.getValue())/(Constants.dpicpixpercm)));
			rocketpic.repaint();
		}
	}
	class fwTL implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			try
			{
				Double fwD = Double.valueOf(fwfld.getText());
				double fw = fwD.doubleValue();
				if( fw < (fwadj.getMinimum()/Constants.dpicpixpercm) || fw > ((fwadj.getMaximum() - fwadj.getBlockIncrement())/Constants.dpicpixpercm) )
				{
					if(fw < (fwadj.getMinimum()/Constants.dpicpixpercm))
						fw = (fwadj.getMinimum()/Constants.dpicpixpercm);
					if(fw > ((fwadj.getMaximum() - fwadj.getBlockIncrement())/Constants.dpicpixpercm) )
						fw = fwadj.getMaximum() - fwadj.getBlockIncrement();
					fwfld.setText(Double.toString(fw));
					
				}
				fwadj.setValue( (int)(fwmod - (fw * Constants.dpicpixpercm)) );

				rocketpic.rocket.setFinWidth(fw);
				rocketpic.repaint();			
			}
			catch(NumberFormatException x)
			{
				fwfld.setText(Double.toString((double)(fwmod - fwadj.getValue())/(Constants.dpicpixpercm))); 
				return;
			}
		}
	}

	//fh listeners
	class fhAL implements AdjustmentListener
	{
		public void adjustmentValueChanged(AdjustmentEvent e)
		{
			if(rocketpic.rocket.getBodyLength() > rocketpic.rocket.getFinLength() + ((fhmod - fhadj.getValue())/Constants.dpicpixpercm) )
			{
				rocketpic.rocket.setFinHeight((double)(fhmod - fhadj.getValue())/Constants.dpicpixpercm);
				fhfld.setText(Double.toString((double)(fhmod - fhadj.getValue())/Constants.dpicpixpercm));

			}
			else
			{
				fhadj.setValue( fhmod - (int)(Constants.dpicpixpercm * (rocketpic.rocket.getBodyLength() - rocketpic.rocket.getFinLength())) );
				fhfld.setText(Double.toString(roundtotenths(rocketpic.rocket.getBodyLength() - rocketpic.rocket.getFinLength())));
				rocketpic.rocket.setFinHeight(rocketpic.rocket.getBodyLength() - rocketpic.rocket.getFinLength());
			}
			rocketpic.repaint();
		}
	}
	class fhTL implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			try
			{
				Double fhD = Double.valueOf(fhfld.getText());
				double fh = fhD.doubleValue();
				if( 	fh > rocketpic.rocket.getBodyLength() - rocketpic.rocket.getFinLength()
					|| fh < (fhadj.getMinimum()/Constants.dpicpixpercm) 
					|| fh > ((fhadj.getMaximum() - fhadj.getBlockIncrement())/Constants.dpicpixpercm) 
				)
				{
					if(fh < (fhadj.getMinimum()/Constants.dpicpixpercm))
						fh = (fhadj.getMinimum()/Constants.dpicpixpercm);
					if(fh > ((fhadj.getMaximum() - fhadj.getBlockIncrement())/Constants.dpicpixpercm) )
						fh = fhadj.getMaximum() - fhadj.getBlockIncrement();
					if(fh > rocketpic.rocket.getBodyLength() - rocketpic.rocket.getFinLength())
						fh = rocketpic.rocket.getBodyLength() - rocketpic.rocket.getFinLength();
					fhfld.setText(Double.toString(fh));
					
				}
				fhadj.setValue( (int)(fhmod - (fh * Constants.dpicpixpercm)) );
				rocketpic.rocket.setFinHeight(fh);
				rocketpic.repaint();			
			}
			catch(NumberFormatException x)
			{
				fhfld.setText(Double.toString((double)(fhmod - fhadj.getValue())/(Constants.dpicpixpercm))); 
				return;
			}
		}
	}

	//z listeners
	class zAL implements AdjustmentListener
	{
		public void adjustmentValueChanged(AdjustmentEvent e)
		{
			rocketpic.dzoom = ((double)(zmod - zadj.getValue())/100.0);
			zfld.setText(Integer.toString(zmod - zadj.getValue()));
			rocketpic.repaint();
		}
	}
	class zTL implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			try
			{
				int z = Integer.parseInt(zfld.getText());
				if( z < zadj.getMinimum() || z > zadj.getMaximum() - zadj.getBlockIncrement() )
				{
					if(z < zadj.getMinimum())
						z = zadj.getMinimum();
					if(z > zadj.getMaximum() - zadj.getBlockIncrement())
						z = zadj.getMaximum() - zadj.getBlockIncrement();
					zfld.setText(Integer.toString(z));	
				}
				zadj.setValue(zmod - z);
				rocketpic.dzoom = ((double)z)/100.0;
				rocketpic.repaint();				
			}
			catch(NumberFormatException x)
			{
				zfld.setText(Integer.toString(zmod - zadj.getValue())); 
				return;
			}
		}
	}
	class fnCL implements ItemListener
	{
		Checkbox thisbox, otherbox;
		int thisfinnumber, otherfinnumber;
		public fnCL(Checkbox thisbox, Checkbox otherbox, int thisfinnumber, int otherfinnumber)
		{
			this.thisbox = thisbox;
			this.otherbox = otherbox;
			this.thisfinnumber = thisfinnumber;
			this.otherfinnumber = otherfinnumber;
		} 
 		public void itemStateChanged(ItemEvent e)
		{
			boolean newstate = thisbox.getState();
			otherbox.setState( !newstate );
			if( newstate == true )
			{
				rocketpic.rocket.setFinNumber(thisfinnumber);
			}
			else
			{
				rocketpic.rocket.setFinNumber(otherfinnumber);
			}
			rocketpic.repaint();
		}
	}
}


class CgCpMOutput
{
	TextField CgOutput;
	TextField CpOutput;
	TextField MOutput;
	CgCpMOutput(TextField cgfld, TextField cpfld, TextField mfld)
	{
		CgOutput = cgfld;
		CpOutput = cpfld;
		MOutput = mfld;

		CgOutput.setBackground(Color.black);
		CpOutput.setBackground(Color.black);
		MOutput.setBackground(Color.black);
		MOutput.setForeground(Color.green);
	}
	void adjust(double cg, double cp, double m, double heightOfStage)
	{
		cg = (double)((int)(100.0 * (cg+heightOfStage)))/100.0;
		cp = (double)((int)(100.0 * (cp+heightOfStage)))/100.0;
		m = (double)((int)(100.0 * 1000.0 * m))/100.0;
		
		if(cg > cp)
		{
			CgOutput.setForeground(Color.green);
			CpOutput.setForeground(Color.green);
		}
		else
		{
			CgOutput.setForeground(Color.red);
			CpOutput.setForeground(Color.red);
		}

		CgOutput.setText(Double.toString(cg));
		CpOutput.setText(Double.toString(cp));
		MOutput.setText(Double.toString(m));
	}
}

class RocketParams
{
	int numStages;
	double noseconedensity, findensitythickness;	
	double recpaymass;
	double bodywidth, noseconelength, parachutediameter;
	double largestEngineWidth;
	char biggestEngine;
	int finnumber;
	double co_o_drag;
	
	int stageFiring;
	
	//index = number of stages dropped from total rocket
	Engine engines[] = new Engine[3];
	double bodylength[] = new double[3];
	double finheight[] = new double[3];
	double finlength[] = new double[3];
	double finwidth[] = new double[3];
	double apparentfinlength[] = new double[3];
	double apparentfinwidth[] = new double[3];
	double cg[] = new double[3];
	double cp[] = new double[3];
	double mass[] = new double[3];



	
	public RocketParams(int nstgs, double ncd, double fdt, Engine e[], double rpmass, double bl, double bw, double ncl, double fl, double fw, double fh, int fn, double pd, double cod) 
	{	
		numStages = nstgs;
		noseconedensity = ncd;
		findensitythickness = fdt;
		engines = e;
		recpaymass = rpmass;
		bodywidth = bw;
		noseconelength = ncl;
		finnumber = fn;
		parachutediameter = pd;
		co_o_drag = cod;
		updateData(bl, fh, fl, fw);
	}
	public void updateData(double bodlen, double fheight, double flength, double fwidth)
	{
		largestEngineWidth = engines[0].width;
		bodylength[0] = bodlen;
		finheight[0] = fheight;
		finlength[0] = flength;
		finwidth[0] = fwidth;
		biggestEngine = engines[0].name.charAt(0);
		if(finnumber == 4)
		{
			//four fins	
			apparentfinlength[0] = flength;
			apparentfinwidth[0] = fwidth;
		}
		else
		{
			//three fins
			apparentfinwidth[0] = fwidth*Math.cos(Constants.radiansperdegree * 30);
			apparentfinlength[0] = (flength/fwidth)*(fwidth - (bodywidth/2)*(1- Math.cos(Constants.radiansperdegree * 30)));		
		}

		
		
		for(int stageIndex = 1; stageIndex < numStages; stageIndex++)
		{
			//largest engine width
			if( engines[stageIndex].width > largestEngineWidth)
			{
				largestEngineWidth = engines[stageIndex].width;
			}
			
			//bodylength
			bodylength[stageIndex] =  bodylength[stageIndex-1]- engines[stageIndex-1].length;

			//finheight
			finheight[stageIndex] = finheight[stageIndex-1] - engines[stageIndex-1].length;
			if(finheight[stageIndex] < 0)
			{
				finheight[stageIndex] = 0.0;
			}

			//finlength
			if(finheight[stageIndex] > 0.0 || finlength[stageIndex-1] == 0.0)
			{
				finlength[stageIndex] = finlength[stageIndex-1];
			}
			else
			{
				finlength[stageIndex] = finlength[stageIndex-1] - (engines[stageIndex-1].length - finheight[stageIndex-1]);
				if(finlength[stageIndex] < 0)
				{
					finlength[stageIndex] = 0.0;
				}
			}

			//finwidth
			if(finheight[stageIndex] > 0.0)
			{
				finwidth[stageIndex] = finwidth[stageIndex-1];
			}
			else
			{
				finwidth[stageIndex] = finlength[stageIndex]*(fwidth/flength);
			}
			
			if(finnumber == 4)
			{
				//four fins	
				apparentfinlength[stageIndex] = finlength[stageIndex];
				apparentfinwidth[stageIndex] = finwidth[stageIndex];
			}
			else
			{
				//three fins
				apparentfinwidth[stageIndex] = finwidth[stageIndex]*Math.cos(Constants.radiansperdegree * 30);
				apparentfinlength[stageIndex] = (flength/fwidth)*(finwidth[stageIndex] - (bodywidth/2)*(1- Math.cos(Constants.radiansperdegree * 30)));		
			}

			if( ((int)biggestEngine) < ((int)engines[stageIndex].name.charAt(0)))
			{
				biggestEngine = engines[stageIndex].name.charAt(0);
			}
			
		}


		
		for(int stagesInCalc =1; stagesInCalc <= numStages; stagesInCalc++)
		{
			calccg(stagesInCalc);
			calccp(stagesInCalc);
		}

	}

	//usually you just need to recalculate cp & cg when you change something, else use one of the special functions below
	public void update()
	{
		for(int stagesInCalc =1; stagesInCalc <= numStages; stagesInCalc++)
		{
			calccg(stagesInCalc);
			calccp(stagesInCalc);
		}
	}
	public int getNumStages()
	{
		return numStages;
	}
	public Engine getEngine(int stage)
	{
		return engines[stage-1];
	}
	public double getBodyLength()
	{
		return bodylength[0];
	}	
	public double getFinHeight()
	{
		return finheight[0];
	}
	public double getFinLength()
	{
		return finlength[0];
	}
	public double getFinWidth()
	{
		return finwidth[0];
	}
	public double getFinNumber()
	{
		return finnumber;
	}

	
	//returns by how many centimeters the rocket changed when new engines added/removed
	//(negative means it got smaller, positive bigger)
	public double setNumStages(int nstgs)
	{
		if(numStages < nstgs)
		{
			//select engines
			for(int engineIndex = numStages-1; engineIndex >= 0; engineIndex--)
			{
				engines[engineIndex+(nstgs-numStages)] = engines[engineIndex];
			}
			Engine defaultBooster;
			switch( biggestEngine )
			{
				case 'A':
				case 'a':
					defaultBooster = (Engine)Constants.engines.elementAt(1);
					break;
				case 'B':
				case 'b':
					defaultBooster = (Engine)Constants.engines.elementAt(4);
					break;
				case 'C':
				case 'c':
					defaultBooster = (Engine)Constants.engines.elementAt(8);
					break;
				case 'D':
				case 'd':
					defaultBooster = (Engine)Constants.engines.elementAt(12);
					break;
				default:
					//case = 1/2A
					defaultBooster = (Engine)Constants.engines.elementAt(1);
					biggestEngine = 'A';
					break;
			}
			
			double lengthOfAddition = 0;
			for(int newstgIndex = numStages+1; newstgIndex <= nstgs; newstgIndex++)
			{
				engines[newstgIndex-numStages-1] = defaultBooster;
				lengthOfAddition = lengthOfAddition + defaultBooster.length;
				
			}
			//add length of engines to fins and bodytube, lengthening the rocket
			numStages = nstgs;
			updateData( bodylength[0]+lengthOfAddition, finheight[0], finlength[0]+lengthOfAddition, finwidth[0] );
			return lengthOfAddition;
		}
		if(numStages > nstgs)
		{
			double lengthOfRemoval = 0;
			for(int oldstgIndex = 0; oldstgIndex < 3; oldstgIndex++)
			{
				if(oldstgIndex < numStages-nstgs)
				{
					lengthOfRemoval = lengthOfRemoval - engines[oldstgIndex].length;
				}
				
				if(oldstgIndex + (numStages-nstgs) < 3 )
				{
					engines[oldstgIndex] = engines[oldstgIndex + (numStages-nstgs)];
				}
				else
				{
					engines[oldstgIndex] = null;
				}
			}
			//subtract length of engines from fins and bodytube, shortening the rocket	
			numStages = nstgs;



			
			updateData( bodylength[0]+lengthOfRemoval, finheight[0], finlength[0]+lengthOfRemoval, finwidth[0] );
			return lengthOfRemoval;
		}
		return 0;
	}
	public void setEngine(int stage, Engine engine)
	{
		engines[stage-1] = engine;
		updateData( bodylength[0], finheight[0], finlength[0], finwidth[0] );
	}
	public void setBodyLength(double bodlen)
	{
		updateData( bodlen, finheight[0], finlength[0], finwidth[0] );
	}
	public void setFinHeight(double fheight)
	{	
		updateData( bodylength[0], fheight, finlength[0], finwidth[0] );
	}
	public void setFinLength(double flength)
	{
		updateData( bodylength[0], finheight[0], flength, finwidth[0] );
	}
	public void setFinWidth(double fwidth)
	{
		updateData( bodylength[0], finheight[0], finlength[0], fwidth );
	}
	public void setFinNumber(int fn)
	{
		finnumber = fn;
		updateData( bodylength[0], finheight[0], finlength[0], finwidth[0] );
	}

	
	double calccg(int stagesInComputation)
	{
		int cgIndex = numStages-stagesInComputation;
		double noseconemass = noseconedensity * (.5*Constants.pi*(bodywidth/2.0)*(bodywidth/2.0)*noseconelength);
		double finmass = findensitythickness * (.5*finlength[cgIndex]*finwidth[cgIndex]);		
		double bodytubemass =  	Constants.bodytubedensity 
					* bodylength[cgIndex]
					* Constants.pi * bodywidth 
					* Constants.bodytubethickness;             
		double engineFactor = 0;
		double totalEngineMass = 0;
		double previousEnginePosition = 0;
		for(int stageIndex = cgIndex; stageIndex < numStages; stageIndex++)
		{
			double engineMass = engines[stageIndex].mass;
			double enginePosition = previousEnginePosition + engines[stageIndex].length/2.0;
			previousEnginePosition = previousEnginePosition + engines[stageIndex].length;
			engineFactor = engineFactor + (engineMass*enginePosition);
			totalEngineMass = totalEngineMass + engineMass;
		}
		/*
		System.out.println("totalEngineMass[" + cgIndex + "]=" + totalEngineMass);
		System.out.println("bodytubemass = " + bodytubemass);
		System.out.println("noseconemass = " +  noseconemass);
		System.out.println("finmass = " + finmass);
		*/
		
		
		cg[cgIndex] = 	(finnumber * finmass * ((finlength[cgIndex]/3.0) + finheight[cgIndex]))   
				+   ( noseconemass * ( noseconelength/3.0 + bodylength[cgIndex] ) )   
				+   ( bodytubemass * (bodylength[cgIndex]/2.0) )   
				+  engineFactor 
				+ ( recpaymass * bodylength[cgIndex] );
		
		mass[cgIndex] = (finnumber*finmass) + noseconemass + bodytubemass + totalEngineMass + recpaymass;
		cg[cgIndex] = cg[cgIndex]/mass[cgIndex];
		mass[cgIndex] = mass[cgIndex] / 1000.0;
		return(cg[cgIndex]);		
	}


	double calccp(int stagesInComputation)
	{
		int cpIndex = numStages-stagesInComputation;
		double finarea = .5 * apparentfinwidth[cpIndex] * apparentfinlength[cpIndex];
		double bodyarea = bodywidth * bodylength[cpIndex];
		double noseconearea = .5 * bodywidth * noseconelength;
		
		cp[cpIndex] =	((2.0 * finarea)*( finlength[cpIndex]/3.0 + finheight[cpIndex]))   
				+ (bodyarea*(bodylength[cpIndex]/2.0))   
				+ (noseconearea*(noseconelength/3.0 + bodylength[cpIndex]));
		cp[cpIndex] = cp[cpIndex]/(2.0*finarea + bodyarea + noseconearea);

		return(cp[cpIndex]);	
	}
}

class RocketCanvas extends Canvas
{
	RocketParams rocket;
	double dzoom;
	CgCpMOutput output;
	int bottom, bodydistfromleft;
	int cgcpViewIndex;
	boolean displayValuesFromBottomOfRocket;
	
	public RocketCanvas( RocketParams rkt, int zoom, int nss, boolean displayFromBottom, CgCpMOutput outp )
	{
		rocket = rkt;
		bottom = 325;
		dzoom = ((double)(zoom))/(100.0);
		output = outp;
		displayValuesFromBottomOfRocket = displayFromBottom;
		setNumStagesShowing(nss);
		
	}
	public void setNumStagesShowing(int nss)
	{
		cgcpViewIndex = rocket.numStages-nss;
		double heightOfStage = 0;
		for(int stageIndex = cgcpViewIndex; stageIndex > 0 && displayValuesFromBottomOfRocket; stageIndex--)
		{
			heightOfStage = heightOfStage + rocket.engines[stageIndex-1].length;
		}
		output.adjust(rocket.cg[cgcpViewIndex], rocket.cp[cgcpViewIndex], rocket.mass[cgcpViewIndex], heightOfStage); 
		repaint();
	}
	public void update(Graphics g)
	{
		paint(g);
	}
	public void paint(Graphics g)
	{
		
		rocket.update();

		Image offScreenImage= createImage(300,350);
		Graphics offScreenGraphic = offScreenImage.getGraphics();
		
		int bl, bw, ncl, fl, fw, fh;
		bl = (int)(dzoom * (rocket.bodylength[0] * Constants.dpicpixpercm));
		bw = (int)(dzoom * (rocket.bodywidth * Constants.dpicpixpercm));
		ncl =(int)(dzoom * (rocket.noseconelength * Constants.dpicpixpercm));
		fl = (int)(dzoom * (rocket.apparentfinlength[0] * Constants.dpicpixpercm));
		fw = (int)(dzoom * (rocket.apparentfinwidth[0] * Constants.dpicpixpercm));
		fh = (int)(dzoom * (rocket.finheight[0] * Constants.dpicpixpercm));

		bodydistfromleft = 10 + fw;		

		int ncxpts[] = {bodydistfromleft, (bodydistfromleft + (bw/2)), (bodydistfromleft + bw ), bodydistfromleft};
		int ncypts[] = {(bottom - bl -1), (bottom - bl - ncl - 1 ), (bottom - bl -1), (bottom - bl -1)};
		
		int lfxpts[] = {(bodydistfromleft - 1), (bodydistfromleft - fw - 1), (bodydistfromleft - 1), (bodydistfromleft -1) };
		int rfxpts[] = {(bodydistfromleft + bw + 1), (bodydistfromleft + bw + fw + 1), (bodydistfromleft + bw + 1), (bodydistfromleft + bw +1)};	
		int fypts[] =  {(bottom - fh), (bottom - fh), (bottom - fl - fh), (bottom - fh) };

		Polygon nosecone = new Polygon(ncxpts, ncypts, ncxpts.length);
		Polygon leftfin = new Polygon(lfxpts, fypts, fypts.length);	
		Polygon rightfin = new Polygon(rfxpts, fypts, fypts.length);
		
		//Erase screen
		offScreenGraphic.setColor(Color.white);
		offScreenGraphic.fillRect(0,0,100,250);

		//Draw body tube
		offScreenGraphic.setColor(Color.green);	
		offScreenGraphic.fillRect(bodydistfromleft, (bottom - bl ), bw, bl);
		offScreenGraphic.drawRect(bodydistfromleft, (bottom - bl ), bw, bl);
		
		//Draw nosecone
		offScreenGraphic.setColor(Color.red);
		offScreenGraphic.drawPolygon(nosecone);
		offScreenGraphic.fillPolygon(nosecone);
		
		//Draw fins (assume 4 on rocket)
		offScreenGraphic.setColor(Color.black); 
		offScreenGraphic.drawPolygon(leftfin);
		offScreenGraphic.fillPolygon(leftfin);
		offScreenGraphic.drawPolygon(rightfin);
		offScreenGraphic.fillPolygon(rightfin);
		offScreenGraphic.drawLine((bodydistfromleft + (bw/2)), (bottom - fh), (bodydistfromleft + (bw/2)), (bottom - fh - fl));	
		

		//draw stage lines- dashed and in purple
		//purple color= #9400D3
		//== 1001 0100 0000 0000 1101 0011
		//== 148, 0, 211
		offScreenGraphic.setColor( new Color(148, 0, 211));
		int cumulativeEngineDistance = 0;
		for(int stageIndex = 1; stageIndex < rocket.numStages; stageIndex++)
		{
			int horizontalDistanceFromTube = (int)(dzoom*(rocket.apparentfinwidth[stageIndex]*Constants.dpicpixpercm));
			cumulativeEngineDistance = cumulativeEngineDistance + (int)(dzoom*(rocket.engines[stageIndex-1].length*Constants.dpicpixpercm));
			if(rocket.finheight[stageIndex]> 0)
			{
				horizontalDistanceFromTube = 0;
			}


			drawDashedLine(	bodydistfromleft-horizontalDistanceFromTube, 
					bottom - cumulativeEngineDistance,
					bodydistfromleft+bw+horizontalDistanceFromTube,
					bottom - cumulativeEngineDistance,
					3,
					offScreenGraphic
					);
			
		}


		
	

		//update cp/cg info in output boxes
		double heightOfStage = 0;
		for(int stageIndex = cgcpViewIndex; stageIndex > 0 && displayValuesFromBottomOfRocket; stageIndex--)
		{
			heightOfStage = heightOfStage + rocket.engines[stageIndex-1].length;
		}	
		output.adjust(rocket.cg[cgcpViewIndex], rocket.cp[cgcpViewIndex], rocket.mass[cgcpViewIndex], heightOfStage);


		//if height not calculated do it now so cp/cg displayed in right spot
		for(int stageIndex = cgcpViewIndex; stageIndex > 0 && !displayValuesFromBottomOfRocket; stageIndex--)
		{
			heightOfStage = heightOfStage + rocket.engines[stageIndex-1].length;
		}
		//draw cg 
		offScreenGraphic.setColor(Color.black);
		offScreenGraphic.drawOval(bodydistfromleft + bw/2 - Constants.identcircwidth/2, bottom - (int)(dzoom * (double)Constants.dpicpixpercm * (rocket.cg[cgcpViewIndex]+heightOfStage)) - Constants.identcircwidth/2, Constants.identcircwidth, Constants.identcircwidth); 
		offScreenGraphic.fillArc(bodydistfromleft + bw/2 - Constants.identcircwidth/2, bottom - (int)(dzoom * (double)Constants.dpicpixpercm * (rocket.cg[cgcpViewIndex]+heightOfStage)) - Constants.identcircwidth/2, Constants.identcircwidth, Constants.identcircwidth, 0, 90);
		offScreenGraphic.fillArc(bodydistfromleft + bw/2 - Constants.identcircwidth/2, bottom - (int)(dzoom * (double)Constants.dpicpixpercm * (rocket.cg[cgcpViewIndex]+heightOfStage)) - Constants.identcircwidth/2, Constants.identcircwidth, Constants.identcircwidth, 180, 90);

		//draw cp 
		offScreenGraphic.drawOval( bodydistfromleft + bw/2 - Constants.identcircwidth/2, bottom - (int)(dzoom * (double)Constants.dpicpixpercm * (rocket.cp[cgcpViewIndex]+heightOfStage)) - Constants.identcircwidth/2, Constants.identcircwidth, Constants.identcircwidth);
		offScreenGraphic.drawOval( bodydistfromleft + bw/2 - Constants.identcircwidth/8, bottom - (int)(dzoom * (double)Constants.dpicpixpercm * (rocket.cp[cgcpViewIndex]+heightOfStage)) - Constants.identcircwidth/8, Constants.identcircwidth/4, Constants.identcircwidth/4);
		offScreenGraphic.fillOval( bodydistfromleft + bw/2 - Constants.identcircwidth/8, bottom - (int)(dzoom * (double)Constants.dpicpixpercm * (rocket.cp[cgcpViewIndex]+heightOfStage)) - Constants.identcircwidth/8, Constants.identcircwidth/4, Constants.identcircwidth/4);		
		
		//display rocket once drawn offscreen
		g.drawImage(offScreenImage,0,0,this);
	}

	
	public void drawDashedLine(int x1, int y1, int x2, int y2, int segmentLength, Graphics g)
	{
		boolean visible = true;
		int xIncrement = (int)Math.sqrt(Math.pow(segmentLength,2)-Math.pow(y2-y1,2));
		int yIncrement = (int)Math.sqrt(Math.pow(segmentLength,2)-Math.pow(x2-x1,2));

		
		boolean increasingX = (x2-x1 >= 0);
		boolean increasingY = (y2-y1 >=0);
		int xFactor = 1;
		int yFactor = 1;
		if(!increasingX)
		{
			xFactor = -1;
		}
		if(!increasingY)
		{
			yFactor = -1;
		}
		
		while( (x1<x2 && increasingX) || (x1>x2 && !increasingX)
			|| (y1<y2 && increasingY) || (y1>y2 && !increasingY)
		)
		{
			int nextX = x1 + xFactor*xIncrement;
			int nextY = y1 + yFactor*yIncrement;
			if(	(nextX>x2 && increasingX) || (nextX<x2 && !increasingX)
				|| (nextY>y2 && increasingY) || (nextY<y2 && !increasingY)
			)
			{
				nextX = x2;
				nextY = y2;
			}
			if(visible)
			{
				g.drawLine(x1, y1, nextX, nextY);
			}
			x1 = nextX;
			y1 = nextY;
			visible = !visible;
		}

	}
}


class LaunchThread extends Thread
{
	LaunchCanvas launchpic;
	TextField timefld, velocityfld,  maxaltfld;	
	RocketParams rckt;
	int percentrealtime;
	AudioClip thrusters;
	AudioClip countdown[];
	boolean pause;

	LaunchThread( RocketParams rckt, int launchangle, double windvelocity, int percentrealtime, AudioClip thrusters, AudioClip countdown[])
	{
		launchpic = new LaunchCanvas(rckt, launchangle, windvelocity);
		this.percentrealtime = percentrealtime;
		this.thrusters = thrusters;
		this.countdown = countdown;
		pause = true;

	 	timefld = new TextField(6);
		timefld.setEditable(false);
		timefld.setBackground( Color.black );
		timefld.setForeground( Color.white );

	 	velocityfld = new TextField(6);
		velocityfld.setEditable(false);
		velocityfld.setBackground( Color.black );
		velocityfld.setForeground( Color.white );

	 	maxaltfld = new TextField(6);
		maxaltfld.setEditable(false);
		maxaltfld.setBackground( Color.black );
		maxaltfld.setForeground( Color.white );
	}

	public void run()
	{

		while(true)
		{
			try
			{	
				if(launchpic.launchphase == 1 && launchpic.timeelapsed == 0)
				{
					launchpic.launchphase = 0;
					launchpic.currentcountdownnumber = 3;
					while(  launchpic.currentcountdownnumber > 0)
					{
						int temp = launchpic.currentcountdownnumber;
						launchpic.repaint();
						countdown[ launchpic.currentcountdownnumber-1].play();
						sleep(1250);
						launchpic.currentcountdownnumber = -1;
						launchpic.repaint();
						sleep(250);
						launchpic.currentcountdownnumber = temp - 1;	
					}
					launchpic.currentcountdownnumber = -1;
					launchpic.launchphase = 1;
				}
				
				launchpic.calculate = true;
				launchpic.repaint();
				if(launchpic.launchphase != 1)			
				{
					sleep( (int)(Constants.deltat * percentrealtime * 10) );
				}
				else
				{
					int timesToPlay = (int)((Constants.deltat*percentrealtime*10)/250);	
					for(int playnumber = 0; playnumber < timesToPlay; playnumber++)
					{
						thrusters.play();
						sleep(250);
					}
					thrusters.play(); 
					sleep( (int)(Constants.deltat * percentrealtime * 10) - timesToPlay*250 );
				}

				launchpic.calculate = false;
				timefld.setText(Double.toString(  launchpic.timeelapsed  ));
				velocityfld.setText(Double.toString( launchpic.lastyvelocity ));
				maxaltfld.setText(Double.toString( launchpic.maxaltitude ));
			
				if(  (launchpic.launchphase > 0 && launchpic.altitude < 0) ||  (launchpic.launchphase == -1 && launchpic.altitude < -.1) )
				{
					pause = true;
				}
				while(pause == true)
				{
					sleep(1000);
				}

			}catch (InterruptedException e){}
		}

	}
}


class LaunchCanvas extends Canvas
{
	boolean calculate;

	RocketParams rocket;
	int launchphase;
	double distance; //meters- much like altitude, but refers to total distance: vertical AND horizontal
	double timeelapsed, engineStartTime; //seconds
	int launchangle;
	double windvelocity;
	int tilt; //angle the rocket is heading after launched- it is launch angle to start with
	int bottom, top, initbodydistfromleft, bodydistfromleft;	
	double lastyvelocity, altitude, lastxvelocity, xdist;
	double maxaltitude;	
	int currentcountdownnumber;

	int lowerStagePositions[];

	public LaunchCanvas(RocketParams rock, int la, double wv)
	{
		calculate = false;
		rocket = rock;
		launchangle = la;
		bottom = 315;
		top = 10;
		windvelocity = wv;
		lowerStagePositions = new int[2];
		initialize();

		
	}
	public void initialize()
	{
		initbodydistfromleft = 25 + (int)(rocket.bodywidth * Constants.lpicpixpercm) + (int)(rocket.finwidth[rocket.stageFiring] * Constants.lpicpixpercm);
		bodydistfromleft = initbodydistfromleft;
		launchphase = 0;
		distance = 0;
		timeelapsed = 0;
		engineStartTime = 0;
		maxaltitude = 0;
		tilt = launchangle;
		lastyvelocity = altitude = xdist =0;
		lastxvelocity = windvelocity *.4809066666666667;
		currentcountdownnumber = -1;
		rocket.stageFiring = 0;
		lowerStagePositions[0] = -1;
		lowerStagePositions[1] = -1;
		
	}
	public void update(Graphics g)
	{
		paint(g);
	}
	public double roundtotenths(double roundee)
	{
		roundee = ((double)(Math.round(roundee * 10.0)))/10.0;
		return(roundee);
	}
	public void paint(Graphics g)
	{
		int oldlaunchphase = launchphase;
		if( !calculate)
		{
			if(timeelapsed == 0)
				launchphase = 0;
			else
				return;
		}
		Image offScreenImage= createImage(500,350);
		Graphics offScreenGraphic = offScreenImage.getGraphics();
		int pivotx, pivoty;
		

		if(launchphase > 0 )
		{
			if(launchphase == 1)
				calcdistance(1);
			else
				calcdistance(0);
		}
		


		switch(launchphase) 
		{
		case 0:
			//paint rocket
			paintrocket(offScreenGraphic, bodydistfromleft, 0, ((launchangle)/90) + 1, launchangle % 90, 0);
			//paint launch rod
			offScreenGraphic.setColor(Color.black);
			offScreenGraphic.drawLine( initbodydistfromleft + 2 , bottom, (int)Math.round(initbodydistfromleft + 2 + (int)Math.round(Constants.lrodlength * Constants.lpicpixpercm * Math.cos(Constants.radiansperdegree * launchangle))), bottom - (int)Math.round(Constants.lrodlength * Constants.lpicpixpercm * Math.sin(Constants.radiansperdegree * launchangle))      );
			
			//paint grass
			offScreenGraphic.setColor(Color.green.darker());
			offScreenGraphic.fillRect(0, bottom, 400, 50);

			if(currentcountdownnumber >= 0)
			{
				Font oldfont = offScreenGraphic.getFont();
				Font bigfont = new Font( oldfont.getName(), Font.BOLD, 50);
				offScreenGraphic.setFont(bigfont);
				Color outputcolor =  Color.red;
				for(int i = 0; i < currentcountdownnumber-1; i++)
				{
					outputcolor = outputcolor.darker();
				}
				offScreenGraphic.setColor( outputcolor );
				offScreenGraphic.drawString("T - " + currentcountdownnumber, 40, 100);
				offScreenGraphic.setFont(oldfont);
			}
			if(!calculate)
			{
				launchphase = oldlaunchphase;
			}
			break;
		case 1:
			
			
			if(altitude < 0)
			{
				altitude = 0;
			}
			//if rocket is not at final position in graphic draw initial scenery and then rocket
			if(altitude * Constants.lpicpixperm <  Constants.rocketfinalheight )
			{
				//paint rocket
				if(xdist > 0  && bodydistfromleft < 225)
					bodydistfromleft = bodydistfromleft + (int)Math.round( (xdist * Constants.lpicpixperm) + initbodydistfromleft - bodydistfromleft);
				pivotx = bodydistfromleft;
				pivoty = (int)Math.round(altitude * Constants.lpicpixperm);				
			
				//paint launchrod
				int lroddistfromleft;
				if( Math.floor(xdist * Constants.lpicpixperm) <= bodydistfromleft-initbodydistfromleft && xdist > 0)
					lroddistfromleft = initbodydistfromleft + 2;
				else
					lroddistfromleft = bodydistfromleft + 2 - (int)(xdist * Constants.lpicpixperm);	
				offScreenGraphic.setColor(Color.black);
				offScreenGraphic.drawLine( lroddistfromleft  , bottom, (int)Math.round(lroddistfromleft + (int)Math.round(Constants.lrodlength * Constants.lpicpixpercm * Math.cos(Constants.radiansperdegree * launchangle))), bottom - (int)Math.round(Constants.lrodlength * Constants.lpicpixpercm * Math.sin(Constants.radiansperdegree * launchangle))      );
				

				//paint grass
				offScreenGraphic.setColor(Color.green.darker());
				offScreenGraphic.fillRect(0, bottom, 400, 50);
			}
			else //else paint rocket in final position
			{
				pivotx = bodydistfromleft;
				pivoty = Constants.rocketfinalheight;
			}
			
			//draw rocket and flames
			paintrocket(offScreenGraphic, pivotx, pivoty, ((tilt)/90) + 1, tilt % 90, 1);
			
			//if rocket is at final position in graphic and altitude is low move ground features away to simulate motion
			if(altitude * Constants.lpicpixperm >= Constants.rocketfinalheight &&  altitude * Constants.lpicpixperm <= 350  && altitude >= maxaltitude)
			{
				//paint launchrod
				int lroddistfromleft;
				if( Math.floor(xdist * Constants.lpicpixperm) <= bodydistfromleft-initbodydistfromleft && xdist > 0)
					lroddistfromleft = initbodydistfromleft + 2;
				else
					lroddistfromleft = bodydistfromleft + 2 - (int)(xdist * Constants.lpicpixperm);		
				int xmod = (int)Math.round(xdist * Constants.lpicpixperm  ) - (bodydistfromleft - initbodydistfromleft);
				int ymod = (int)Math.round(Constants.lpicpixperm * altitude ) - Constants.rocketfinalheight;
				offScreenGraphic.setColor(Color.black);
				offScreenGraphic.drawLine( lroddistfromleft  ,bottom  + ymod , (int)Math.round(lroddistfromleft + (int)Math.round(Constants.lrodlength * Constants.lpicpixpercm * Math.cos(Constants.radiansperdegree * launchangle))) , bottom - (int)Math.round(Constants.lrodlength * Constants.lpicpixpercm * Math.sin(Constants.radiansperdegree * launchangle)) + ymod      );
					
				//paint grass
				offScreenGraphic.setColor(Color.green.darker());
				offScreenGraphic.fillRect(0, (int)(bottom + ymod), 400, 50);
			}
			if(rocket.cg[rocket.stageFiring] < rocket.cp[rocket.stageFiring] )
			{
				launchphase = -1;
			}
			if(timeelapsed > .1 && maxaltitude <= 0)
			{
				maxaltitude= 0;
			}
			if(timeelapsed-engineStartTime > rocket.engines[rocket.stageFiring].totalburntime )
			{
				if(rocket.stageFiring+1 == rocket.getNumStages())
				{
					launchphase = 2;
				}
				else
				{
					lowerStagePositions[rocket.stageFiring] = Constants.stagefallrate;
					engineStartTime = engineStartTime + rocket.engines[rocket.stageFiring].totalburntime;
					rocket.stageFiring++;
				}
			}
			
			break;
		case -1:
			double velocity;
			timeelapsed = timeelapsed + Constants.deltat;
			
			//if rocket is not at final position in graphic draw initial scenery and then rocket
			if(altitude * Constants.lpicpixperm <  Constants.rocketfinalheight )
			{
				//adjust distance from left
				if(maxaltitude <= altitude  && xdist > 0  && bodydistfromleft < 225)
					bodydistfromleft = bodydistfromleft + (int)Math.round( (xdist * Constants.lpicpixperm) + initbodydistfromleft - bodydistfromleft);


				//paint launchrod
				int lroddistfromleft;
				if( Math.floor(xdist * Constants.lpicpixperm) <= bodydistfromleft-initbodydistfromleft && xdist > 0)
					lroddistfromleft = initbodydistfromleft + 2;
				else
					lroddistfromleft = bodydistfromleft + 2 - (int)(xdist * Constants.lpicpixperm);	
				offScreenGraphic.setColor(Color.black);
				offScreenGraphic.drawLine( lroddistfromleft  , bottom, (int)Math.round(lroddistfromleft + (int)Math.round(Constants.lrodlength * Constants.lpicpixpercm * Math.cos(Constants.radiansperdegree * launchangle))), bottom - (int)Math.round(Constants.lrodlength * Constants.lpicpixpercm * Math.sin(Constants.radiansperdegree * launchangle))      );
				
				//paint grass
				offScreenGraphic.setColor(Color.green.darker());
				offScreenGraphic.fillRect(0, bottom, 400, 50);
			}
			if(altitude * Constants.lpicpixperm >= Constants.rocketfinalheight &&  altitude * Constants.lpicpixperm <= 350)
			{
				//paint launchrod
				int lroddistfromleft;
				if( Math.floor(xdist * Constants.lpicpixperm) <= bodydistfromleft-initbodydistfromleft && xdist > 0)
					lroddistfromleft = initbodydistfromleft + 2;
				else
					lroddistfromleft = bodydistfromleft + 2 - (int)(xdist * Constants.lpicpixperm);		
				int xmod = (int)Math.round(xdist * Constants.lpicpixperm  ) - (bodydistfromleft - initbodydistfromleft);
				int ymod = (int)Math.round(Constants.lpicpixperm * altitude ) - Constants.rocketfinalheight;
				offScreenGraphic.setColor(Color.black);
				offScreenGraphic.drawLine( lroddistfromleft  ,bottom  + ymod , (int)Math.round(lroddistfromleft + (int)Math.round(Constants.lrodlength * Constants.lpicpixpercm * Math.cos(Constants.radiansperdegree * launchangle))) , bottom - (int)Math.round(Constants.lrodlength * Constants.lpicpixpercm * Math.sin(Constants.radiansperdegree * launchangle)) + ymod      );
								
				//paint grass
				offScreenGraphic.setColor(Color.green.darker());
				offScreenGraphic.fillRect(0, (int)(bottom + ymod), 400, 50);
			}

			if(altitude * Constants.lpicpixperm <  Constants.rocketfinalheight && altitude >= maxaltitude && (timeelapsed < .1 || maxaltitude > 0))
			{
				calcdistance(1);
			}
			else
			{
				if(tilt != 270)
				{
					tilt--;
					if(tilt == -1)
						tilt = 359;
					if(tilt < 270)
						calcdistance(1);
					else
					{
						double stheta = -1 * Math.sin(Constants.radiansperdegree * tilt);
			 			velocity = (rocket.mass[rocket.stageFiring] * lastyvelocity) + Constants.deltat * ((stheta * rocket.engines[rocket.stageFiring].avthrust2) + (9.8 * rocket.mass[rocket.stageFiring]) - (stheta * .7 * .5 * 1.22 * lastyvelocity * lastyvelocity * (Constants.pi * (rocket.bodywidth/200.0) * (rocket.bodywidth/200.0)) ));     
						lastyvelocity = velocity/rocket.mass[rocket.stageFiring];
						altitude = altitude - (stheta * lastyvelocity * Constants.deltat);
					}
					xdist = xdist + (1.0/Constants.lpicpixperm);
					if(bodydistfromleft < 225)
						bodydistfromleft++;
				}
				if(tilt == 270 )
				{
			 		velocity = (rocket.mass[rocket.stageFiring] * lastyvelocity) + Constants.deltat * (( rocket.engines[rocket.stageFiring].avthrust2) + (9.8 * rocket.mass[rocket.stageFiring]) - ( .7 * .5 * 1.22 * lastyvelocity * lastyvelocity * (Constants.pi * (rocket.bodywidth/200.0) * (rocket.bodywidth/200.0)) ));     
					lastyvelocity = velocity/rocket.mass[rocket.stageFiring];
					altitude = altitude - ( lastyvelocity * Constants.deltat);
				}
				if(altitude <= -.1)
				{
					offScreenGraphic.setColor(Color.black);
					offScreenGraphic.drawString("Your Rocket Was Unstable!!!", 25, 80);
					altitude = -.11;
				}
			}
			//if rocket is not at final position in graphic draw initial scenery and then rocket
			if(altitude * Constants.lpicpixperm <  Constants.rocketfinalheight )
			{
					pivoty = (int)Math.round(altitude * Constants.lpicpixperm);				
			}
			else //else paint rocket in final position
			{
				pivoty = Constants.rocketfinalheight;
			}
				
			//draw rocket and flames
			paintrocket(offScreenGraphic, bodydistfromleft , pivoty, ((tilt)/90) + 1, tilt % 90, 1);
				
			break;
		case 2:

			if(maxaltitude == 0 && tilt > 0)
				altitude = 0;

			//if rocket is not at final position in graphic draw initial scenery and then rocket
			if(altitude * Constants.lpicpixperm <  Constants.rocketfinalheight )
			{
				//paint rocket
				//System.out.print("(bdfl=" + bodydistfromleft + ", xdist=" + xdist + ", ibdfl=" + initbodydistfromleft + ", alt=" + altitude +", maxalt=" + maxaltitude + ")\n");
				if(maxaltitude <= altitude  && xdist > 0  && bodydistfromleft < 225 )
					bodydistfromleft = bodydistfromleft + (int)Math.round( (xdist * Constants.lpicpixperm) + initbodydistfromleft - bodydistfromleft);
				

				pivotx = bodydistfromleft;
				pivoty = (int)Math.round(altitude * Constants.lpicpixperm);				
			
				//paint launchrod
				int lroddistfromleft;
				if( Math.floor(xdist * Constants.lpicpixperm) <= bodydistfromleft-initbodydistfromleft && xdist > 0)
					lroddistfromleft = initbodydistfromleft + 2;
				else
					lroddistfromleft = bodydistfromleft + 2 - (int)(xdist * Constants.lpicpixperm);		
				offScreenGraphic.setColor(Color.black);
				offScreenGraphic.drawLine( lroddistfromleft  , bottom, (int)Math.round(lroddistfromleft + (int)Math.round(Constants.lrodlength * Constants.lpicpixpercm * Math.cos(Constants.radiansperdegree * launchangle))), bottom - (int)Math.round(Constants.lrodlength * Constants.lpicpixpercm * Math.sin(Constants.radiansperdegree * launchangle))      );
				

				//paint grass
				offScreenGraphic.setColor(Color.green.darker());
				offScreenGraphic.fillRect(0, bottom, 400, 50);
			}
			else //else paint rocket in final position
			{
				pivoty = Constants.rocketfinalheight;
			}
			
			//draw rocket without flames 
			paintrocket(offScreenGraphic, bodydistfromleft, pivoty, ((tilt)/90) + 1, tilt % 90, 0);
			
			//if rocket is at final position in graphic and altitude is low move ground features away to simulate motion
			if(altitude * Constants.lpicpixperm >= Constants.rocketfinalheight &&  altitude * Constants.lpicpixperm <= 350)
			{						
				//paint launchrod
				int lroddistfromleft;
				if( Math.floor(xdist * Constants.lpicpixperm) <= bodydistfromleft-initbodydistfromleft && xdist > 0)
					lroddistfromleft = initbodydistfromleft + 2;
				else
					lroddistfromleft = bodydistfromleft + 2 - (int)(xdist * Constants.lpicpixperm);		
				int xmod = (int)Math.round(xdist * Constants.lpicpixperm  ) - (bodydistfromleft - initbodydistfromleft);
				int ymod = (int)Math.round(Constants.lpicpixperm * altitude ) - Constants.rocketfinalheight;
				offScreenGraphic.setColor(Color.black);
				offScreenGraphic.drawLine( lroddistfromleft  ,bottom  + ymod , (int)Math.round(lroddistfromleft + (int)Math.round(Constants.lrodlength * Constants.lpicpixpercm * Math.cos(Constants.radiansperdegree * launchangle))) , bottom - (int)Math.round(Constants.lrodlength * Constants.lpicpixpercm * Math.sin(Constants.radiansperdegree * launchangle)) + ymod      );
					

				//paint grass
				offScreenGraphic.setColor(Color.green.darker());
				offScreenGraphic.fillRect(0, (int)(bottom + ymod ), 400, 50);				
			}
			
			if( lastyvelocity < 5 && tilt != 270 && altitude > 0)
			{
				int t;
				if(  (int)(((lastyvelocity/5.0)) * 90) >= 0.0)
					t = launchangle - (int)(( 1.0 - (lastyvelocity/5.0)) * launchangle);
				else
					t = 360 + (int)(((lastyvelocity/5.0)) * 90.0);
				if(t > 180 && t < 270)
					t = 270;
				
				if (tilt - t >= 0)
					bodydistfromleft = bodydistfromleft + (tilt - t);
				else 
					bodydistfromleft = bodydistfromleft + (360 - t);
				tilt = t;
			}
			if( timeelapsed-engineStartTime > rocket.engines[rocket.stageFiring].totalburntime + rocket.engines[rocket.stageFiring].delaytime)
			{
				lastyvelocity = 0;
				tilt = 270;
				lastxvelocity = windvelocity * .4809066666666667;
				launchphase = 3;
			}
			if(altitude <= 0)
			{
				painttouchdown(offScreenGraphic, timeelapsed);
				altitude = -.1;
			}
			break;
		case 3:
						

			//if rocket is not at final position in graphic draw initial scenery and then rocket
			if(altitude * Constants.lpicpixperm <  Constants.rocketfinalheight )
			{
				//paint rocket
				pivoty = (int)Math.round(altitude * Constants.lpicpixperm);				
			
				//paint launchrod
				int lroddistfromleft;
				if( Math.floor(xdist * Constants.lpicpixperm) <= bodydistfromleft-initbodydistfromleft )
					lroddistfromleft = initbodydistfromleft + 2;
				else
					lroddistfromleft = bodydistfromleft - (int)(xdist * Constants.lpicpixperm);		
				offScreenGraphic.setColor(Color.black);
				offScreenGraphic.drawLine( lroddistfromleft  , bottom, (int)Math.round(lroddistfromleft + (int)Math.round(Constants.lrodlength * Constants.lpicpixpercm * Math.cos(Constants.radiansperdegree * launchangle))), bottom - (int)Math.round(Constants.lrodlength * Constants.lpicpixpercm * Math.sin(Constants.radiansperdegree * launchangle))      );
				
				
				//paint grass
				offScreenGraphic.setColor(Color.green.darker());
				offScreenGraphic.fillRect(0, bottom, 400, 50);
			}
			else //else paint rocket in final position
			{
				pivoty = Constants.rocketfinalheight;
			}

			//draw rocket and flames
			paintsplitrocket(offScreenGraphic, bodydistfromleft, pivoty );
			
			//if rocket is at final position in graphic and altitude is low move ground features away to simulate motion
			if(altitude * Constants.lpicpixperm >= Constants.rocketfinalheight &&  altitude * Constants.lpicpixperm <= 350)
			{
				
				//paint launchrod
				int lroddistfromleft;
				if( Math.floor(xdist * Constants.lpicpixperm) <= bodydistfromleft-initbodydistfromleft )
					lroddistfromleft = initbodydistfromleft + 2;
				else
					lroddistfromleft = bodydistfromleft + 2 - (int)(xdist * Constants.lpicpixperm);		
				int xmod = (int)Math.round(xdist * Constants.lpicpixperm  ) - (bodydistfromleft - initbodydistfromleft);
				int ymod = (int)Math.round(Constants.lpicpixperm * altitude ) - Constants.rocketfinalheight;
				offScreenGraphic.setColor(Color.black);
				offScreenGraphic.drawLine( lroddistfromleft  ,bottom  + ymod , (int)Math.round(lroddistfromleft + (int)Math.round(Constants.lrodlength * Constants.lpicpixpercm * Math.cos(Constants.radiansperdegree * launchangle))) , bottom - (int)Math.round(Constants.lrodlength * Constants.lpicpixpercm * Math.sin(Constants.radiansperdegree * launchangle)) + ymod      );
					

				//paint grass
				offScreenGraphic.setColor(Color.green.darker());
				offScreenGraphic.fillRect(0, (int)(bottom + ymod), 400, 50);				
			}

			if(altitude <= 0)
			{
				painttouchdown(offScreenGraphic, timeelapsed);
				altitude = -.1;
			}
			break;
		}
		paintaltitude(offScreenGraphic, altitude);
		paintdistance(offScreenGraphic, xdist);
		g.drawImage(offScreenImage,0,0,this);
	}
	void calcdistance(int engineon)
	{
		for( int deltaTInterval = 0; deltaTInterval < 100; deltaTInterval++)
		{
			double xcomp;
			double ycomp;
			double cd;
			double avthrust;
			timeelapsed = timeelapsed + (Constants.deltat/100);


 			if(timeelapsed < rocket.engines[rocket.stageFiring].burntime1)
				avthrust = rocket.engines[rocket.stageFiring].avthrust1;
			else
				avthrust = rocket.engines[rocket.stageFiring].avthrust2;
		
			if(launchphase < 3)
				cd = rocket.co_o_drag;
			else
				cd = 1.7;

			ycomp = calcaltitude(engineon, avthrust, cd);
			xcomp = calcxdist(engineon, avthrust, cd);
			distance =  Math.sqrt(ycomp * ycomp + xcomp * ycomp);
		}
	}
	double calcaltitude(int engineon, double avthrust, double cd)
	{
		double alt, velocity;
		double stheta = Math.sin(Constants.radiansperdegree * tilt); 
		
		double rocketradius;
		if(launchphase == 3)
		{
			rocketradius = rocket.parachutediameter/2.0;
		}
		else
		{
			rocketradius = rocket.bodywidth/200.0;
		}
		
		double mass = rocket.mass[rocket.stageFiring]-(((engineon+1)%2)*rocket.engines[rocket.stageFiring].fuelmass)/1000.0;
		//System.out.println("mass=" + mass);
		if(lastyvelocity != 0 && stheta >= 0)
		{
			velocity =	(mass * lastyvelocity) 
					+ (Constants.deltat/100) 
					* (
						(stheta * engineon * avthrust) 
						- (9.8 * mass) 
						- ( (lastyvelocity/Math.abs(lastyvelocity) )
						* stheta 
						* cd 
						* .5
						* 1.22 
						* lastyvelocity 
						* lastyvelocity 
						* (Constants.pi * rocketradius * rocketradius) 
					)
				);     
		}
		else
		{
			velocity = 	(mass * lastyvelocity) 
					+ (Constants.deltat/100) 
					* (
						(stheta * engineon * avthrust) 
						- (9.8 * mass) 
						- ( 	stheta 
					  		* cd 
							* .5
							* 1.22
							* lastyvelocity 
							* lastyvelocity 
							* (Constants.pi * rocketradius * rocketradius) 
					)
				);
		}
		velocity = velocity/mass;

		alt = (velocity * (Constants.deltat/100)) + altitude;
		
		if( alt < 0)
		{
			alt = -.01;
			velocity = 0;
		}
		
		lastyvelocity = velocity;
		if(alt > altitude && alt > 0)
			maxaltitude = alt;
		altitude = alt;
		return(altitude);
	}
	double calcxdist(int engineon, double avthrust, double cd) 
	{
		double dist, velocity;
		double ctheta = Math.cos(Constants.radiansperdegree * tilt); 
		
		double rocketradius;
		if(launchphase == 3)
			rocketradius = .30;
		else
			rocketradius = rocket.bodywidth/200.0;

		double mass = rocket.mass[rocket.stageFiring]-(((engineon+1)%2)*rocket.engines[rocket.stageFiring].fuelmass)/1000.0;
		
		velocity = (mass * lastxvelocity) + (Constants.deltat/100) * ((ctheta * engineon * avthrust) - (ctheta * cd * .5 * 1.22 * lastxvelocity * lastxvelocity * (Constants.pi * rocketradius * rocketradius) )) ;     
		velocity = velocity/mass;
		
		dist = ((Constants.deltat/100) * velocity) + xdist;
		lastxvelocity = velocity;
		xdist = dist;
		return(xdist);
	}
	void painttouchdown(Graphics g, double time)
	{
		if(launchphase == 3)
			g.drawString("Touchdown!", 40, 50);
		else
			g.drawString("Your Rocket Crashed!", 40, 50);
		g.drawString("Total flight time was " + roundtotenths(time) + " seconds.", 40, 80);
		g.drawString("Maximum altitude attained by rocket was:", 40, 110);
		g.drawString( roundtotenths(maxaltitude) + " meters.", 40, 125);
		
	}
	void paintaltitude(Graphics g, double altit)
	{
		g.setColor(Color.black);
		double alt = altit - ((double)Constants.rocketfinalheight / Constants.lpicpixperm);
		double labelpos, labelalt, initlabelpos, initlabelalt, remainder;
		if(alt < 0)
		{
			alt = 0;
			initlabelpos = 0;
			initlabelalt = 0;
		}
		else
		{
			alt = altit;
			remainder = 1 - ((alt/Constants.lpicmperinc) - (double)((int)(alt /Constants.lpicmperinc)));
			initlabelpos = Constants.rocketfinalheight + Math.round(Constants.lpicpixperm * Constants. lpicmperinc * remainder );
			initlabelalt = alt + (remainder * Constants.lpicmperinc);
		}
		for(labelalt = initlabelalt, labelpos = initlabelpos; labelpos < 350; labelalt = labelalt + Constants.lpicmperinc, labelpos = labelpos + (Constants.lpicmperinc * Constants.lpicpixperm) )
		{
			g.drawString( Integer.toString((int)Math.round(labelalt)) + "m", 0, bottom - (int)Math.round(labelpos) );
		}
		for(labelalt = initlabelalt, labelpos = initlabelpos; labelpos > 0; labelalt = labelalt - Constants.lpicmperinc, labelpos = labelpos - (Constants.lpicmperinc * Constants.lpicpixperm) )
		{
			g.drawString( Integer.toString((int)Math.round(labelalt)) + "m", 0, bottom - (int)Math.round(labelpos) );
		}


	}
	void paintdistance(Graphics g, double dist) 
	{
		double xstart;
		g.setColor(Color.black);
		double labelpos, labeldist, initlabelpos, initlabeldist, remainder;

		
		if(altitude < ((double)Constants.rocketfinalheight / Constants.lpicpixperm) && maxaltitude <= altitude && xdist >= 0 && bodydistfromleft < 225)
		{
			xstart = 0;
			initlabelpos = initbodydistfromleft;
			initlabeldist = 0;
		}
		else
		{
			xstart = xdist;
			remainder = 1 - ((xstart/Constants.lpicmperinc) - (double)((int)(xstart /Constants.lpicmperinc)));
			initlabelpos = bodydistfromleft + Math.round(Constants.lpicpixperm * Constants.lpicmperinc * remainder );
			initlabeldist = xstart + (remainder * Constants.lpicmperinc);
			//System.out.print("( ilp:" + initlabelpos + ", bdfl:" + bodydistfromleft + ", r:" + remainder + ")\n");
		}

		
		for(labeldist = initlabeldist, labelpos = initlabelpos; labelpos < 350; labeldist = labeldist + Constants.lpicmperinc, labelpos = labelpos + (Constants.lpicmperinc * Constants.lpicpixperm) )
		{
			g.drawString( Integer.toString((int)Math.round(labeldist)) + "m", (int)Math.round(labelpos), bottom + 30 );
		}
		for(labeldist = initlabeldist, labelpos = initlabelpos; labelpos > 0; labeldist = labeldist - Constants.lpicmperinc, labelpos = labelpos - (Constants.lpicmperinc * Constants.lpicpixperm) )
		{
			g.drawString( Integer.toString((int)Math.round(labeldist)) + "m",  (int)Math.round(labelpos), bottom + 30);
		}


	}



	void paintrocket(Graphics g, int basexpos, int baseypos, int quadrant, int angle, int drawflames)
	{
		int xmod, ymod, pivot;
		int ang = angle;
		xmod = ymod = pivot = 1;
		if(quadrant == 2 || quadrant == 3)
			xmod = -1;
		if(quadrant == 3 || quadrant == 4)
			ymod = -1;
		if(quadrant == 3 || quadrant == 1)
		{
			pivot = -1;
			ang = 90 - ang;
		}
		//initialize arrays to contain x & y body points
		int bxpts[] = {0,0,0,0,0};
		int bypts[] = {0,0,0,0,0};
		bxpts[0] = basexpos;
		bypts[0] = bottom - baseypos;
		bxpts[1] = (int)Math.round(bxpts[0] + (xmod * rocket.bodylength[rocket.stageFiring] * Constants.lpicpixpercm * Math.cos(Constants.radiansperdegree*((double)(90-ang)))));
		bypts[1] = (int)Math.round(bypts[0] - (ymod * rocket.bodylength[rocket.stageFiring] * Constants.lpicpixpercm * Math.sin(Constants.radiansperdegree*((double)(90-ang)))));
		bxpts[2] = (int)Math.round(bxpts[1] + (pivot * xmod * rocket.bodywidth * Constants.lpicpixpercm * Math.sin(Constants.radiansperdegree*((double)(90-ang)))));
		bypts[2] = (int)Math.round(bypts[1] + (pivot * ymod * rocket.bodywidth * Constants.lpicpixpercm * Math.cos(Constants.radiansperdegree*((double)(90-ang)))));
		bxpts[3] = (int)Math.round(bxpts[2] - (xmod * rocket.bodylength[rocket.stageFiring] * Constants.lpicpixpercm * Math.cos(Constants.radiansperdegree*((double)(90-ang)))));
		bypts[3] = (int)Math.round(bypts[2] + (ymod * rocket.bodylength[rocket.stageFiring] * Constants.lpicpixpercm * Math.sin(Constants.radiansperdegree*((double)(90-ang)))));
		bxpts[4] = bxpts[0];
		bypts[4] = bypts[0];

		
		int ncxpts[] = {bxpts[1], bxpts[2], (int)Math.round((double)(bxpts[1] + bxpts[2])/2.0) + (int)Math.round(rocket.noseconelength * xmod * Constants.lpicpixpercm * Math.sin(Constants.radiansperdegree*((double)(ang)) )), bxpts[1]};
		int ncypts[] = {bypts[1], bypts[2], (int)Math.round((double)(bypts[1] + bypts[2])/2.0) - (int)Math.round(rocket.noseconelength * ymod * Constants.lpicpixpercm * Math.cos(Constants.radiansperdegree*((double)(ang)) )), bypts[1]};	
		
		
		int f1xpts[] = {0, 0, 0, 0};
		int f1ypts[] = {0, 0, 0, 0};
		f1xpts[0] = (int)Math.round(bxpts[0] + (xmod * rocket.finheight[rocket.stageFiring] * Constants.lpicpixpercm * Math.sin(Constants.radiansperdegree*((double)ang))));
		f1ypts[0] = (int)Math.round(bypts[0] - (ymod * rocket.finheight[rocket.stageFiring] * Constants.lpicpixpercm * Math.cos(Constants.radiansperdegree*((double)ang))));
		f1xpts[1] = (int)Math.round(f1xpts[0] + (xmod * rocket.finlength[rocket.stageFiring] * Constants.lpicpixpercm * Math.sin(Constants.radiansperdegree*((double)ang))));
		f1ypts[1] = (int)Math.round(f1ypts[0] - (ymod * rocket.finlength[rocket.stageFiring] * Constants.lpicpixpercm * Math.cos(Constants.radiansperdegree*((double)ang))));
		f1xpts[2] = (int)Math.round(f1xpts[0] - (xmod * pivot * rocket.finwidth[rocket.stageFiring] * Constants.lpicpixpercm * Math.cos(Constants.radiansperdegree*((double)ang))));
		f1ypts[2] = (int)Math.round(f1ypts[0] - (ymod * pivot * rocket.finwidth[rocket.stageFiring] * Constants.lpicpixpercm * Math.sin(Constants.radiansperdegree*((double)ang))));
		f1xpts[3] = f1xpts[0];
		f1ypts[3] = f1ypts[0];	

		int f2xpts[] = {0, 0, 0, 0};
		int f2ypts[] = {0, 0, 0, 0};
		f2xpts[0] = (int)Math.round(bxpts[3] + (xmod * rocket.finheight[rocket.stageFiring] * Constants.lpicpixpercm * Math.sin(Constants.radiansperdegree*((double)ang))));
		f2ypts[0] = (int)Math.round(bypts[3] - (ymod * rocket.finheight[rocket.stageFiring] * Constants.lpicpixpercm * Math.cos(Constants.radiansperdegree*((double)ang))));
		f2xpts[1] = (int)Math.round(f2xpts[0] + (xmod * rocket.finlength[rocket.stageFiring] * Constants.lpicpixpercm * Math.sin(Constants.radiansperdegree*((double)ang))));
		f2ypts[1] = (int)Math.round(f2ypts[0] - (ymod * rocket.finlength[rocket.stageFiring] * Constants.lpicpixpercm * Math.cos(Constants.radiansperdegree*((double)ang))));
		f2xpts[2] = (int)Math.round(f2xpts[0] + (xmod * pivot * rocket.finwidth[rocket.stageFiring] * Constants.lpicpixpercm * Math.cos(Constants.radiansperdegree*((double)ang))));
		f2ypts[2] = (int)Math.round(f2ypts[0] + (ymod * pivot * rocket.finwidth[rocket.stageFiring] * Constants.lpicpixpercm * Math.sin(Constants.radiansperdegree*((double)ang))));
		f2xpts[3] = f2xpts[0];
		f2ypts[3] = f2ypts[0];	

		int f3xpts[] = {0,0};
		int f3ypts[] = {0,0};
		f3xpts[0] = (int)Math.round((bxpts[0] + bxpts[3])/2) + (int)Math.round(xmod * rocket.finheight[rocket.stageFiring] * Constants.lpicpixpercm * Math.sin(Constants.radiansperdegree*((double)ang)));
		f3ypts[0] = (int)Math.round(((double)bypts[0] + (double)bypts[3])/2.0) - (int)Math.round(ymod * rocket.finheight[rocket.stageFiring] * Constants.lpicpixpercm * Math.cos(Constants.radiansperdegree*((double)ang)));
		f3xpts[1] = f3xpts[0] + (int)Math.round(xmod * rocket.finlength[rocket.stageFiring] * Constants.lpicpixpercm * Math.sin(Constants.radiansperdegree*((double)ang)));
		f3ypts[1] = f3ypts[0] - (int)Math.round(ymod * rocket.finlength[rocket.stageFiring] * Constants.lpicpixpercm * Math.cos(Constants.radiansperdegree*((double)ang)));

		Polygon bodytube = new Polygon(bxpts, bypts, bxpts.length);		
		Polygon nosecone = new Polygon(ncxpts, ncypts, ncxpts.length);
		Polygon fin1 = new Polygon(f1xpts, f1ypts, f1xpts.length);
		Polygon fin2 = new Polygon(f2xpts, f2ypts, f2xpts.length);
		


		for(int stageIndex = 0; stageIndex < 2; stageIndex++)
		{
			if(lowerStagePositions[stageIndex] > 0 && lowerStagePositions[stageIndex] < bottom-basexpos)
			{
				//draw lower stage falling off
				int topOfTube = bottom-baseypos + lowerStagePositions[stageIndex];
				int bottomOfTube = topOfTube + (int)Math.round(rocket.engines[stageIndex].length * Constants.lpicpixpercm);
				int bottomOfFins = bottomOfTube-(int)Math.round(rocket.finheight[stageIndex] * Constants.lpicpixpercm);
				int bodyWidth = (int)Math.round(rocket.bodywidth * Constants.lpicpixpercm);
				int lowerFinDistanceFromBody = (int)Math.round(rocket.apparentfinwidth[stageIndex] * Constants.lpicpixpercm);
				int upperFinDistanceFromBody = (int)Math.round(rocket.apparentfinwidth[stageIndex+1] * Constants.lpicpixpercm);
				
				//draw bodytube segment
				int tubePtsX[] = { basexpos, basexpos+bodyWidth, basexpos+bodyWidth, basexpos};
				int tubePtsY[] = { bottomOfTube, bottomOfTube, topOfTube, topOfTube };
				Polygon stageTube = new Polygon(tubePtsX, tubePtsY, tubePtsX.length);
				g.setColor(Color.green);
				g.drawPolygon(stageTube);
				g.fillPolygon(stageTube);
				
				//if fins on stage draw them
				if(bottomOfFins > topOfTube)
				{
					int stageFinPtsY[] = { bottomOfFins, bottomOfFins, topOfTube, topOfTube };
					int stageLeftFinPtsX[] = { basexpos-lowerFinDistanceFromBody, basexpos, basexpos, basexpos-upperFinDistanceFromBody };
					int stageRightFinPtsX[] = { basexpos+bodyWidth+lowerFinDistanceFromBody, basexpos+bodyWidth, basexpos+bodyWidth, basexpos+bodyWidth+upperFinDistanceFromBody };
					Polygon stageLeftFin = new Polygon(stageLeftFinPtsX, stageFinPtsY, stageFinPtsY.length);
					Polygon stageRightFin = new Polygon(stageRightFinPtsX, stageFinPtsY, stageFinPtsY.length);
					
					g.setColor(Color.black);
					g.drawPolygon(stageLeftFin);
					g.fillPolygon(stageLeftFin);
					g.drawPolygon(stageRightFin);
					g.fillPolygon(stageRightFin);
					g.drawLine( basexpos + bodyWidth/2, bottomOfFins, basexpos + bodyWidth/2, topOfTube);
				}
				lowerStagePositions[stageIndex] = lowerStagePositions[stageIndex] + Constants.stagefallrate;
			}
		}



		
		if(drawflames == 1)
		{
			double yelflamlength = 10;
			double yelflamwidth = 3;
			double random = Math.random();
			int yelflamxpts[] = {0, 0, 0, 0};
			int yelflamypts[] = {0, 0, 0, 0};
			yelflamxpts[0] = bxpts[0] + (int)Math.round(pivot * xmod * (((rocket.bodywidth * Constants.lpicpixpercm) - yelflamwidth)/2.0) * Math.sin(Constants.radiansperdegree*((double)(90-ang))));
			yelflamypts[0] = bypts[0] + (int)Math.round(pivot * ymod * (((rocket.bodywidth * Constants.lpicpixpercm) - yelflamwidth)/2.0) * Math.cos(Constants.radiansperdegree*((double)(90-ang))));		
			yelflamxpts[1] = yelflamxpts[0] + (int)Math.round(pivot * xmod * yelflamwidth * Math.sin(Constants.radiansperdegree*((double)(90-ang))));
			yelflamypts[1] = yelflamypts[0] + (int)Math.round(pivot * ymod * yelflamwidth * Math.cos(Constants.radiansperdegree*((double)(90-ang))));
			yelflamxpts[2] = yelflamxpts[0] - (int)Math.round(xmod * yelflamlength * Math.cos(Constants.radiansperdegree*((double)(90-ang)))) + (int)Math.round(pivot * xmod * yelflamwidth * random * Math.sin(Constants.radiansperdegree*((double)(90-ang))));
			yelflamypts[2] = yelflamypts[0] + (int)Math.round(ymod * yelflamlength * Math.sin(Constants.radiansperdegree*((double)(90-ang)))) + (int)Math.round(pivot * ymod * yelflamwidth * random * Math.cos(Constants.radiansperdegree*((double)(90-ang))));
			yelflamxpts[3] = yelflamxpts[0];
			yelflamypts[3] = yelflamypts[0];
			Polygon yelflame = new Polygon(yelflamxpts, yelflamypts, yelflamxpts.length);
			g.setColor(Color.yellow);
			g.drawPolygon(yelflame);
			g.setColor(Color.red.brighter());
			g.fillPolygon(yelflame);
		}

		g.setColor(Color.green);
		g.drawPolygon(bodytube);
		g.fillPolygon(bodytube);

		g.setColor(Color.red);
		g.drawPolygon(nosecone);
		g.fillPolygon(nosecone);

		g.setColor(Color.black);
		g.drawPolygon(fin1);
		g.fillPolygon(fin1);

		g.drawPolygon(fin2);
		g.fillPolygon(fin2);
		
		g.drawLine(f3xpts[0], f3ypts[0], f3xpts[1], f3ypts[1]);	

	}
	void paintsplitrocket(Graphics g, int basexpos, int baseypos)
	{
		
		int xmod, pivot;
		int ymod = -1; 
		int ang = 0;
		xmod = pivot = 1;
		

		//initialize arrays to contain x & y body points
		int bxpts[] = {0,0,0,0,0};
		int bypts[] = {0,0,0,0,0};		
		bxpts[0] = basexpos;
		bypts[0] = bottom - baseypos;
		bxpts[1] = (int)Math.round(bxpts[0] + (xmod * rocket.bodylength[rocket.stageFiring] * Constants.lpicpixpercm * Math.cos(Constants.radiansperdegree*((double)(90-ang)))));
		bypts[1] = (int)Math.round(bypts[0] - (ymod * rocket.bodylength[rocket.stageFiring] * Constants.lpicpixpercm * Math.sin(Constants.radiansperdegree*((double)(90-ang)))));
		bxpts[2] = (int)Math.round(bxpts[1] + (pivot * xmod * rocket.bodywidth * Constants.lpicpixpercm * Math.sin(Constants.radiansperdegree*((double)(90-ang)))));
		bypts[2] = (int)Math.round(bypts[1] + (pivot * ymod * rocket.bodywidth * Constants.lpicpixpercm * Math.cos(Constants.radiansperdegree*((double)(90-ang)))));
		bxpts[3] = (int)Math.round(bxpts[2] - (xmod * rocket.bodylength[rocket.stageFiring] * Constants.lpicpixpercm * Math.cos(Constants.radiansperdegree*((double)(90-ang)))));
		bypts[3] = (int)Math.round(bypts[2] + (ymod * rocket.bodylength[rocket.stageFiring] * Constants.lpicpixpercm * Math.sin(Constants.radiansperdegree*((double)(90-ang)))));
		bxpts[4] = bxpts[0];
		bypts[4] = bypts[0];

		
		int ncxpts[] = {bxpts[3] + 3, bxpts[3] + 3 + (int)Math.round(rocket.bodywidth * Constants.lpicpixpercm), (int)Math.round((double)((bxpts[3] + bxpts[3] + 6 +(rocket.bodywidth * Constants.lpicpixpercm))/2.0) ), bxpts[3] + 3};
		int ncypts[] = {bypts[3], bypts[3] , (int)Math.round((double)( bypts[0]  + (rocket.noseconelength * Constants.lpicpixpercm ) )), bypts[3]};	
		

		int f1xpts[] = {0, 0, 0, 0};
		int f1ypts[] = {0, 0, 0, 0};
		f1xpts[0] = bxpts[1];
		f1ypts[0] = (int)Math.round(bypts[1] - (rocket.finheight[rocket.stageFiring] * Constants.lpicpixpercm) );
		f1xpts[1] = (int)Math.round(f1xpts[0] - (rocket.finwidth[rocket.stageFiring] * Constants.lpicpixpercm) );
		f1ypts[1] = f1ypts[0];
		f1xpts[2] = f1xpts[0];
		f1ypts[2] = (int)Math.round(f1ypts[0] - ((rocket.finheight[rocket.stageFiring] + rocket.finlength[rocket.stageFiring]) * Constants.lpicpixpercm));
		f1xpts[3] = f1xpts[0];
		f1ypts[3] = f1ypts[0];	

		int f2xpts[] = {0, 0, 0, 0};
		int f2ypts[] = {0, 0, 0, 0};
		f2xpts[0] = bxpts[2];
		f2ypts[0] = (int)Math.round(bypts[2] - (rocket.finheight[rocket.stageFiring] * Constants.lpicpixpercm) );
		f2xpts[1] = (int)Math.round(f2xpts[0] + (rocket.finwidth[rocket.stageFiring] * Constants.lpicpixpercm) );
		f2ypts[1] = f2ypts[0];
		f2xpts[2] = f2xpts[0];
		f2ypts[2] = (int)Math.round(f2ypts[0] - ((rocket.finheight[rocket.stageFiring] + rocket.finlength[rocket.stageFiring]) * Constants.lpicpixpercm));
		f2xpts[3] = f2xpts[0];
		f2ypts[3] = f2ypts[0];	

		int f3xpts[] = {0,0};
		int f3ypts[] = {0,0};
		f3xpts[0] = (int)Math.round((bxpts[0] + bxpts[3])/2) + (int)Math.round(xmod * rocket.finheight[rocket.stageFiring] * Constants.lpicpixpercm * Math.sin(Constants.radiansperdegree*((double)ang)));
		f3ypts[0] = (int)Math.round(((double)bypts[1] + (double)bypts[2])/2.0) - (int)Math.round(ymod * rocket.finheight[rocket.stageFiring] * Constants.lpicpixpercm * Math.cos(Constants.radiansperdegree*((double)ang)));
		f3xpts[1] = f3xpts[0] + (int)Math.round(xmod * rocket.finlength[rocket.stageFiring] * Constants.lpicpixpercm * Math.sin(Constants.radiansperdegree*((double)ang)));
		f3ypts[1] = f3ypts[0] + (int)Math.round(ymod * rocket.finlength[rocket.stageFiring] * Constants.lpicpixpercm * Math.cos(Constants.radiansperdegree*((double)ang)));


		int nexusx = bxpts[0] + 5;
		int nexusy = bypts[0] - 5;
		


		for(int stageIndex = 0; stageIndex < 2; stageIndex++)
		{
			if(lowerStagePositions[stageIndex] > 0 && lowerStagePositions[stageIndex] < bottom-basexpos)
			{
				//draw lower stage falling off
				int topOfTube = bottom-baseypos + lowerStagePositions[stageIndex];
				int bottomOfTube = topOfTube + (int)Math.round(rocket.engines[stageIndex].length * Constants.lpicpixpercm);
				int bottomOfFins = bottomOfTube-(int)Math.round(rocket.finheight[stageIndex] * Constants.lpicpixpercm);
				int bodyWidth = (int)Math.round(rocket.bodywidth * Constants.lpicpixpercm);
				int lowerFinDistanceFromBody = (int)Math.round(rocket.apparentfinwidth[stageIndex] * Constants.lpicpixpercm);
				int upperFinDistanceFromBody = (int)Math.round(rocket.apparentfinwidth[stageIndex+1] * Constants.lpicpixpercm);
				
				//draw bodytube segment
				int tubePtsX[] = { basexpos, basexpos+bodyWidth, basexpos+bodyWidth, basexpos};
				int tubePtsY[] = { bottomOfTube, bottomOfTube, topOfTube, topOfTube };
				Polygon stageTube = new Polygon(tubePtsX, tubePtsY, tubePtsX.length);
				g.setColor(Color.green);
				g.drawPolygon(stageTube);
				g.fillPolygon(stageTube);
				
				//if fins on stage draw them
				if(bottomOfFins > topOfTube)
				{
					int stageFinPtsY[] = { bottomOfFins, bottomOfFins, topOfTube, topOfTube };
					int stageLeftFinPtsX[] = { basexpos-lowerFinDistanceFromBody, basexpos, basexpos, basexpos-upperFinDistanceFromBody };
					int stageRightFinPtsX[] = { basexpos+bodyWidth+lowerFinDistanceFromBody, basexpos+bodyWidth, basexpos+bodyWidth, basexpos+bodyWidth+upperFinDistanceFromBody };
					Polygon stageLeftFin = new Polygon(stageLeftFinPtsX, stageFinPtsY, stageFinPtsY.length);
					Polygon stageRightFin = new Polygon(stageRightFinPtsX, stageFinPtsY, stageFinPtsY.length);
					
					g.setColor(Color.black);
					g.drawPolygon(stageLeftFin);
					g.fillPolygon(stageLeftFin);
					g.drawPolygon(stageRightFin);
					g.fillPolygon(stageRightFin);
					g.drawLine( basexpos + bodyWidth/2, bottomOfFins, basexpos + bodyWidth/2, topOfTube);
				}
				lowerStagePositions[stageIndex] = lowerStagePositions[stageIndex] + Constants.stagefallrate;
			}
		}




		
		//draw shockcord
		g.setColor(Color.black);
		g.drawLine(bxpts[0], bypts[0], nexusx, nexusy);
		g.drawLine(ncxpts[2], ncypts[0], nexusx, nexusy);

		Polygon bodytube = new Polygon(bxpts, bypts, bxpts.length);
		Polygon nosecone = new Polygon(ncxpts, ncypts, ncxpts.length);		
		Polygon fin1 = new Polygon(f1xpts, f1ypts, f1xpts.length);
		Polygon fin2 = new Polygon(f2xpts, f2ypts, f2xpts.length);
		
		//draw parachute
		g.setColor(Color.black);
		g.drawLine(bxpts[3]- (int)Math.round( ((rocket.parachutediameter *100.0)/2.0) * Constants.lpicpixpercm), bypts[3] - 20, nexusx, nexusy);
		g.drawLine(bxpts[3] + (int)Math.round( ((rocket.parachutediameter *100.0)/2.0) * Constants.lpicpixpercm), bypts[3] - 20, nexusx, nexusy);
		g.drawLine(nexusx, bypts[3] - 20, nexusx, nexusy);
		g.setColor(Color.blue);
		g.fillArc(bxpts[3] - (int)Math.round( ((rocket.parachutediameter *100.0)/2.0) * Constants.lpicpixpercm), bypts[3] - 30, (int)Math.round(2.0 * ((rocket.parachutediameter *100.0)/2.0) * Constants.lpicpixpercm), 20, 0, 180);
				



		g.setColor(Color.green);
		g.drawPolygon(bodytube);
		g.fillPolygon(bodytube);

		g.setColor(Color.red);
		g.drawPolygon(nosecone);
		g.fillPolygon(nosecone);

		g.setColor(Color.black);
		g.drawPolygon(fin1);
		g.fillPolygon(fin1);

		g.drawPolygon(fin2);
		g.fillPolygon(fin2);
		
		g.drawLine(f3xpts[0], f3ypts[0], f3xpts[1], f3ypts[1]);			
		
		
	}


}



