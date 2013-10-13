package velodyne2d;

import gridmap_generic.GridmapMatrix;
import gridmap_generic.HeightCell;
import gridmap_generic.OccupyCell;

import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;

import javax.media.opengl.GLAnimatorControl;
import javax.media.opengl.awt.GLCanvas;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import VelodyneDataIO.LidarFrameFactory;
import VelodyneView.AnimatorStopper;
import VelodyneView.LidarFrameProcessor;

import com.jogamp.opengl.util.FPSAnimator;

public class LidarViewerMain2D {
	// Define constants for the top-level container
		private static String TITLE = "3D Lidar";  // window's title
		private static final int CANVAS_WIDTH = 800;  // width of the drawable
		private static final int CANVAS_HEIGHT =1200; // height of the drawable
		private static final int FPS = 15; // animator's target frames per second
		
//		private ViewerType viewType; 
		
		static final float startTime=0; //138;//0;
		/** The entry main() method to setup the top-level container and animator */
		public LidarViewerMain2D(ViewerType viewType) {
			LidarFrameFactory lfFac = null;
			LidarFrameProcessor processor = null;
			try{//raw velodyne data
				//lfFac=new LidarFrameFactory(new File("/home/qichi/Qichi_Velodyne/AidedINS/realtime/data/VELODYNE_agg_raw_road_use_midstate_intensity.dat"));
				//lfFac=new LidarFrameFactory(new File("/home/qichi/Qichi_Velodyne/processed_data/low_object_0_0.3.dat"));
				lfFac=new LidarFrameFactory(new File("/home/qichi/Qichi_Velodyne/processed_data/iowa_big/mid_object_0.3_1.dat"));
				//lfFac=new LidarFrameFactory(new File("/home/qichi/Qichi_Velodyne/processed_data/frame.dat"));
				//GridmapMatrix<HeightCell> heightMatrix = GridmapMatrix.loadGridmapMatrix(new File("/home/qichi/Qichi_Velodyne/map/HeightMap/lidar_frame_lowest5"), new HeightCell(10), true);
				//GridmapMatrix<OccupyCell> cellMatrix = GridmapMatrix.loadGridmapMatrix(new File("/home/qichi/Qichi_Velodyne/map/OccupyMap/high_1_conn_0.3_3"), new OccupyCell(), true);
				
				processor = new LidarFrameProcessor(lfFac, null, null);
				processor.getReady(startTime);
				
			}catch(Exception e){
				e.printStackTrace();
				if(processor!=null) processor.stop();
				System.exit(-1);
			}
			GLCanvas canvas=null;
			if(viewType==ViewerType.detection){
				canvas = new LidarGLViewerForDetection(processor);
			}else{
				canvas = new LidarGLViewerForTracking(processor);
			}
			canvas.setPreferredSize(new Dimension(CANVAS_WIDTH, CANVAS_HEIGHT));

			// Create a animator that drives canvas' display() at the specified FPS.
			final FPSAnimator animator = new FPSAnimator(canvas, FPS, true);

			// Create the top-level container
			final JFrame frame = new JFrame(); // Swing's JFrame or AWT's Frame
			frame.getContentPane().add(canvas);
			frame.addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosing(WindowEvent e) {
					// Use a dedicate thread to run the stop() to ensure that the
					// animator stops before program exits.
					new AnimatorStopper(animator).start();
				}
			});
			frame.setTitle(TITLE);
			frame.pack();
			frame.setVisible(true);
			animator.start(); // start the animation loop
		}
		
		public static void main(String[] args) {
//			ViewerType viewType = ViewerType.detection;
			if(args[0].equals("-d")){
				final ViewerType viewType = ViewerType.detection;
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						new LidarViewerMain2D(viewType);  // run the constructor
					}
				});
			}else if(args[0].equals("-t")){
				final ViewerType viewType = ViewerType.tracking;
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						new LidarViewerMain2D(viewType);  // run the constructor
					}
				});
			}else{
				System.err.println("USAGE: -d(detection)/-t(tracking)");
			}
			// Run the GUI codes in the event-dispatching thread for thread safety
			
		}
}

enum ViewerType {detection, tracking}; 

