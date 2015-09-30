package musicPlayer;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashSet;
import java.util.Random;

import javafx.application.Application;
import javafx.embed.swing.JFXPanel;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.ListModel;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import net.miginfocom.swing.MigLayout;

import javax.swing.JButton;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.GridLayout;

import javax.swing.JLabel;

import java.awt.Component;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.FlowLayout;

import javax.swing.JScrollPane;
import javax.swing.JList;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;


public class MusicPlayer extends JFrame {
	
	public class SongListCellRenderer extends DefaultListCellRenderer {
		  public Component getListCellRendererComponent(
			        JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
			        JLabel label = (JLabel)super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

			        
			       File f = (File)value;
			       label.setText(f.getName());

			       return label;
			    }
	}
	
	Random rn;
	MediaPlayer mPlayer;
	DefaultListModel<File> model;
	HashSet<File> songList;
	JList<File> list;
	File nowPlaying;

	private JPanel contentPane;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MusicPlayer frame = new MusicPlayer();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	void log(Object object) {
		System.out.println(object);
	}
	
	void changeTrack(File now) {
		nowPlaying = now;
	}
	

	
	void saveModel() {
		try {
			FileOutputStream fileOut = new FileOutputStream("songList.ser");
			ObjectOutputStream out = new ObjectOutputStream(fileOut);
			
			out.writeObject(model);
			out.close();
			fileOut.close();
			log("serialized model");
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	void readModel() {
		if(new File("songList.ser").exists()) {
			try {
				FileInputStream fileIn = new FileInputStream("songList.ser");		
				ObjectInputStream in = new ObjectInputStream(fileIn);
				
				model = (DefaultListModel<File>)in.readObject();
				
				for(int i = 0 ; i < model.size() ; i++) {
					File f = model.get(i);
					songList.add(f);
				}
				
				in.close();
				fileIn.close();
				log("deserialized model");
			
			} catch (IOException e) {
				e.printStackTrace();
				return;
			} catch (ClassNotFoundException e) {
				log("Class Song not found");
				e.printStackTrace();
				return;
			}
		}
	}
	
	void playSelectedTrack() {
		Media hit = new Media(list.getSelectedValue().toURI().toASCIIString());
		
		if(mPlayer != null) {
			mPlayer.stop();
		}
		
		mPlayer = new MediaPlayer(hit);
		mPlayer.play();
	}
	
	void downloadFile(String URI) throws IOException {
		String ytget = "youtube-dl -q -x --audio-format mp3 -o \"~/Music/YT/%(title)s.%(ext)s\"";
		Runtime.getRuntime().exec(ytget + " " + URI);
	}
	
	void setTrack(int idx) {
		if(idx >= 0 && idx < model.size()) {
			list.setSelectedIndex(idx);
			playSelectedTrack();
		}
	}
	
	void randomTrack() {
		if(!model.isEmpty()) {
			int next = rn.nextInt(model.getSize());
			setTrack(next);
		}
	}
	
	
	void nextTrack() {
		int nxt = (list.getSelectedIndex() + 1) % model.size();
		setTrack(nxt);
	}
	
	void prevTrack() {
		int prev = (list.getSelectedIndex() - 1 + model.size()) % model.size();
		setTrack(prev);
	}
	
	String fileExtension(File f) {
		String filename = f.getName();
		int lst = filename.lastIndexOf('.') + 1;
		String extension = "";
		if(lst < filename.length())
			extension = filename.substring(lst);
		return extension;
	}
	
	boolean isAudio(String ext) {
		if(ext.equalsIgnoreCase("mp3"))
			return true;
		if(ext.equalsIgnoreCase("wav"))
			return true;
		
		return false;
	}
	
	void addFile(File add) {
		String extension = fileExtension(add);
		
		if(isAudio(extension) && !songList.contains(add)) {
			songList.add(add);
			model.addElement(add);
			log("file" + add.toString() + "succesfully added");
		}
	}
	
	void addDirectory(File dir) {
		File[] files = dir.listFiles();
		
		for(File f: files) {
			if(f.isFile()) {
				addFile(f);
			} else {
				addDirectory(f);
			}
		}
	}
	
	/**
	 * Create the frame.
	 */
	public MusicPlayer() {
		
		this.addWindowListener(new WindowListener() {
			
			@Override
			public void windowOpened(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void windowIconified(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void windowDeiconified(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void windowDeactivated(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void windowClosing(WindowEvent e) {
				saveModel();
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void windowClosed(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void windowActivated(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		
		new JFXPanel();
		
		rn = new Random();
		mPlayer = null;
		model = new DefaultListModel<File>();
		songList = new HashSet<File>();
		readModel();
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 600, 600);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		
		JButton button = new JButton("+");
		button.setBounds(391, 223, 44, 25);
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				FileNameExtensionFilter filter = new FileNameExtensionFilter(
				        "Audio files", "mp3", "wav");
				chooser.setFileFilter(filter);
				chooser.setMultiSelectionEnabled(true);
				chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
	
				int returnVal = chooser.showOpenDialog(getParent());
	
				if(returnVal == JFileChooser.APPROVE_OPTION) {
					for(File f: chooser.getSelectedFiles()) {
						if(f.isFile()) {
							addFile(f);
						} else if(f.isDirectory()) {
							addDirectory(f);
						}
					}
				} else {
					log("there was a problem adding files");
				}

			}
		});
		contentPane.setLayout(null);
		
		JButton button_2 = new JButton(">");
		button_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				nextTrack();
			}
		});
		button_2.setBounds(458, 109, 44, 25);
		contentPane.add(button_2);
		contentPane.add(button);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(11, 11, 368, 285);
		contentPane.add(scrollPane);
		
		list = new JList<File>(model);
		list.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent arg0) {
			}
		});
		list.setCellRenderer(new SongListCellRenderer());
		scrollPane.setViewportView(list);
		
		JButton btnPlay = new JButton("Play");
		btnPlay.setBounds(385, 24, 117, 25);
		btnPlay.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(list.isSelectionEmpty()) {
					randomTrack();
				} else {
					playSelectedTrack();
				}
			}
		});
		contentPane.add(btnPlay);
		
		JButton btnPause = new JButton("Pause");
		btnPause.setBounds(385, 72, 117, 25);
		btnPause.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(mPlayer != null) {
					mPlayer.pause();
				}
			}
		});
		contentPane.add(btnPause);
		
		JButton button_1 = new JButton("-");
		button_1.setBounds(463, 223, 39, 25);
		button_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				File remove = list.getSelectedValue();
				
				boolean skip = false;
				if(mPlayer.getMedia().getSource() == remove.toURI().toString()) {
					mPlayer.stop();
					skip = true;
				}
				
				model.removeElement(remove);
				songList.remove(remove);
				
				if(skip) {
					randomTrack();
				}
			}
		});
		contentPane.add(button_1);
		
		JButton button_3 = new JButton("<");
		button_3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				prevTrack();
			}
		});
		button_3.setBounds(391, 109, 44, 25);
		contentPane.add(button_3);
		
		JButton btnRandom = new JButton("Random");
		btnRandom.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				randomTrack();
			}
		});
		btnRandom.setBounds(391, 161, 117, 25);
		contentPane.add(btnRandom);
		
		JButton btnClearList = new JButton("Clear list");
		btnClearList.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				model.clear();
				songList.clear();
			}
		});
		btnClearList.setBounds(391, 271, 117, 25);
		contentPane.add(btnClearList);
	}
}