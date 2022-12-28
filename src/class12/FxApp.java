package class12;
// �w�Дԍ�1116190054���� ���@�D�l
//���ׂĎ����ł���
import java.awt.Point;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.FlowPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

public class FxApp extends Application implements EventHandler, Map.GameEndListener {
	private Label statusLabel, gameLabel;
	private Button playButton;
	private MenuItem openItem = new MenuItem("�J��");
	private MenuItem saveItem = new MenuItem("�ۑ�");
	private Stage stage;
	private int duration = 100; // �A�j���[�V�������s�~���b
	double time = 0.0; // �o�ߎ��ԁi�b�j
	private Map map; // �}�b�v
	private Timeline timeline; // �A�j���[�V�����̓���������
	private Creature creature;
	MenuBar menuBar1 = new MenuBar();
	MenuBar menuBar2 = new MenuBar();
	MenuItem[] mi2 = new  MenuItem[2];
	FlowPane footerFlowPane = new FlowPane();
	BorderPane borderPane = new BorderPane();
	private boolean isRunning = false;
	public static void main(String[] args){
		launch(args);
		}
	@Override
	public void start(Stage primaryStage) throws Exception {
		stage = primaryStage;
		// Menu
		
		Menu menu = new Menu("�t�@�C��");
		menuBar1.getMenus().add(menu);

		menu.getItems().add(openItem);
		menu.getItems().add(saveItem);
		openItem.setOnAction(this);
		saveItem.setOnAction(this);
		menuBar2 = new MenuBar();
	    Menu mn2 =new Menu("HP���x��");
	    
	    mi2[0]=new MenuItem("����");
	    
	    mi2[1]=new MenuItem("�Ȃ�");
	    mn2.getItems().addAll(mi2[0],mi2[1]);
	    menuBar2.getMenus().addAll(mn2);
	    mi2[0].setOnAction(this); 
	    mi2[1].setOnAction(this);
		// label
		statusLabel = new Label();
		statusLabel.setTextFill(Color.BLACK);
		statusLabel.setFont(Font.font("Serif", 24));
		// �g�b�v�p�l��
		FlowPane topFlowPane = new FlowPane();
		topFlowPane.getChildren().add(menuBar1);
		topFlowPane.getChildren().add(menuBar2);
		topFlowPane.getChildren().add(statusLabel);
		topFlowPane.setHgap(10);
		
		// map
		
		map = new Map(24, 16, 30, 30, 30, 30);
		map.addEventHandler(MouseEvent.MOUSE_CLICKED, this);
		map.setGameEndListener(this);
		// button
		playButton = new Button(" �J�n ");
		playButton.setFont(Font.font("Serif", 24));
		playButton.setOnAction(this);
		gameLabel = new Label();
		gameLabel.setTextFill(Color.BLACK);
		gameLabel.setFont(Font.font("Serif", 24));
		// �t�b�^�[�p�l��
		
		footerFlowPane.getChildren().add(playButton);
		footerFlowPane.getChildren().add(gameLabel);
		footerFlowPane.setAlignment(Pos.CENTER);
		footerFlowPane.setHgap(10);
		footerFlowPane.setBackground(new Background(new BackgroundFill(Color.GREENYELLOW,CornerRadii.EMPTY,Insets.EMPTY)));
		
		borderPane.setTop(topFlowPane);
		borderPane.setCenter(map);
		borderPane.setBottom(footerFlowPane);
		// �V�[���̍쐬
		Scene sc = new Scene(borderPane, map.getWidth(), map.getHeight() + 80);
		sc.setOnKeyPressed(this);
		// �V�[�����X�e�[�W�ɒǉ�
		primaryStage.setScene(sc);
		// �X�e�[�W�̕\��
		primaryStage.setTitle("���K���|�[�g�ۑ�12 �w�Дԍ� 1116190054���� ���@�D�l");
		primaryStage.show();
		// ��ʂ̏����`��
		updateStatusLabel();
		map.paint();
		startApp();
	
    }



private void startApp() {
	timeline = new Timeline(new KeyFrame(
			new Duration(duration),
			new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				map.requestFocus();
				if (isRunning) {
				map.nextStep();
				time += duration / 1000.0;
				time = (int) ((1000 * time) + 0.01) / 1000.0;
				}
				updateStatusLabel();
				
				map.paint();
			    alert();
			}
			}
			));
			timeline.setCycleCount(Timeline.INDEFINITE);
			timeline.play();

}

public void handle(Event event) {
	if (event instanceof KeyEvent) {
		KeyEvent e = (KeyEvent) event;
		KeyCode k = e.getCode();
		switch (k) {
		case UP:map.moveSanta(8);
		map.paint();
		break;
		case DOWN:map.moveSanta(2);
		map.paint();
		break;
		case LEFT:map.moveSanta(4);
		map.paint();
		break;
		case RIGHT:map.moveSanta(6);
		map.paint();
		break;
		default:;
		
		}
	}
	else if (event.getTarget().equals(map)) {
        MouseEvent e = (MouseEvent) event;
        double x = e.getX();
        double y = e.getY();
        
        switch(e.getButton()) {
        case PRIMARY:
        	map.setCandy(x, y);
        	break;
        case SECONDARY:
        	map.setCandy(x, y);
        	break;
		default:
			break;
        }
	}
	else if (event.getTarget().equals(playButton)) {
		if (isRunning) {
			//timeline.pause();
			playButton.setText(" �J�n ");
			gameLabel.setText("");
			isRunning=false;
		
		}
		else {
			isRunning=true;
			//timeline.play();
			playButton.setText(" ��~ ");
			gameLabel.setText("�Q�[����");
		
		}
	} 
	else if (event.getTarget().equals(openItem)) {
		FileChooser fileChooser = new FileChooser();
		File file = fileChooser.showOpenDialog(stage);
		map.setField(loadFile(file));
		updateStatusLabel();
		} 
	else if (event.getTarget().equals(saveItem)) {
		FileChooser fileChooser = new FileChooser();
		File file = fileChooser.showSaveDialog(stage);

		saveFile(file, map.getField());
		}
	else if(event.getTarget().equals(mi2[0])) {
		updateStatusLabel();
		map.setHpLabelVisible(true);
	}
    else if(event.getTarget().equals(mi2[1])) {
    	updateStatusLabel1();
    	map.setHpLabelVisible(false);
	}



}
private void alert() {
	Avatar[][] field = map.getField() ;
	
	
	
	if(map.getSanta().getPositionY()>0||map.getSanta().getPositionY()<=map.mapHeight||map.getSanta().getPositionX()>0||map.getSanta().getPositionX()<=map.mapWidth) {
		if(field[map.getSanta().getPositionY()-1][map.getSanta().getPositionX()-1] instanceof Bat||field[map.getSanta().getPositionY()-1][map.getSanta().getPositionX()-1] instanceof Reindeer) {
			footerFlowPane.setBackground(new Background(new BackgroundFill(Color.RED,CornerRadii.EMPTY,Insets.EMPTY)));
		}
		else if(field[map.getSanta().getPositionY()-1][map.getSanta().getPositionX()] instanceof Bat||field[map.getSanta().getPositionY()-1][map.getSanta().getPositionX()] instanceof Reindeer) {
			footerFlowPane.setBackground(new Background(new BackgroundFill(Color.RED,CornerRadii.EMPTY,Insets.EMPTY)));
		}
		else if(field[map.getSanta().getPositionY()-1][map.getSanta().getPositionX()+1] instanceof Bat||field[map.getSanta().getPositionY()-1][map.getSanta().getPositionX()+1] instanceof Reindeer) {
			footerFlowPane.setBackground(new Background(new BackgroundFill(Color.RED,CornerRadii.EMPTY,Insets.EMPTY)));
		}
		else if(field[map.getSanta().getPositionY()][map.getSanta().getPositionX()-1] instanceof Bat||field[map.getSanta().getPositionY()][map.getSanta().getPositionX()-1] instanceof Reindeer) {
			footerFlowPane.setBackground(new Background(new BackgroundFill(Color.RED,CornerRadii.EMPTY,Insets.EMPTY)));
		}
		else if(field[map.getSanta().getPositionY()][map.getSanta().getPositionX()+1] instanceof Bat||field[map.getSanta().getPositionY()][map.getSanta().getPositionX()+1] instanceof Reindeer) {
			footerFlowPane.setBackground(new Background(new BackgroundFill(Color.RED,CornerRadii.EMPTY,Insets.EMPTY)));
		}
		else if(field[map.getSanta().getPositionY()+1][map.getSanta().getPositionX()-1] instanceof Bat||field[map.getSanta().getPositionY()+1][map.getSanta().getPositionX()-1] instanceof Reindeer) {
			footerFlowPane.setBackground(new Background(new BackgroundFill(Color.RED,CornerRadii.EMPTY,Insets.EMPTY)));
		}
		else if(field[map.getSanta().getPositionY()+1][map.getSanta().getPositionX()] instanceof Bat||field[map.getSanta().getPositionY()+1][map.getSanta().getPositionX()] instanceof Reindeer) {
			footerFlowPane.setBackground(new Background(new BackgroundFill(Color.RED,CornerRadii.EMPTY,Insets.EMPTY)));
		}
		else if(field[map.getSanta().getPositionY()+1][map.getSanta().getPositionX()+1] instanceof Bat||field[map.getSanta().getPositionY()+1][map.getSanta().getPositionX()+1] instanceof Reindeer) {
			footerFlowPane.setBackground(new Background(new BackgroundFill(Color.RED,CornerRadii.EMPTY,Insets.EMPTY)));
		}
		else {
			footerFlowPane.setBackground(new Background(new BackgroundFill(Color.GREENYELLOW,CornerRadii.EMPTY,Insets.EMPTY)));
		}
	}
	
	
}
private void updateStatusLabel() {
	String str = "16�s24��@�k" +time + "�l�b�@�N�b�L�[�F"+map.getCookieCount()+"�@�L�����f�B�F"+map.getCandyCount() ;
    
			statusLabel.setText(str);

}
private void updateStatusLabel1() {
	String str = "16�s24��@�k" +time + "�l�b�@" ;
    
			statusLabel.setText(str);

}
private void saveFile(File file, Avatar[][] field) {
	try {
		PrintWriter out = new PrintWriter(file);
		for (int y = 0; y < map.mapHeight; y++) {
		StringBuilder sb = new StringBuilder();
		for (int x = 0; x < map.mapWidth; x++) {
			Avatar a = field[y][x];
			String print = "n";
			if (a instanceof Santa) {
			print = "s";
			} else if (a instanceof Bat) {
			print = "b";
			} else if (a instanceof Reindeer) {
			print = "r";
			} else if (a instanceof Tree) {
			print = "t";
			} else if (a instanceof Cookie) {
			print = "c";
			}
			else if (a instanceof Candy) {
			print = "d";
		    }
		    if (y != map.mapWidth - 1) print = print + " ";
		       sb.append(print);
		    }
		    System.out.println(sb.toString());
		    out.println(sb.toString());
		    }
		    out.flush();
		    gameLabel.setText("�i" + file.getName() + "�j�ɕۑ����܂����D");
		    out.close();
		    } catch (FileNotFoundException e) {
		    e.printStackTrace();
		    }
		

}
private Avatar[][] loadFile(File file) {
	Avatar[][] field = new Avatar[map.mapHeight][map.mapWidth];

	for (int y = 0; y < map.mapHeight; y++) {
		for (int x = 0; x < map.mapWidth; x++) {
			field[y][x] = null;
			}
	
	}
	int x = 0;
	try {
		BufferedReader br = new BufferedReader(new FileReader(file));
		String line;
		int y = 0;
		while ((line = br.readLine()) != null) {
			String[] ss = line.split(" ");
			for (x = 0; x < ss.length; x++) {
				String s = ss[x];
				switch (s) {
				case "s":
				field[y][x] = new Santa(new Point(x, y));
				break;
				case "b":
				field[y][x] = new Bat(new Point(x, y));
				break;
				case "r":
				field[y][x] = new Reindeer(new Point(x, y));
				break;
				case "t":
				field[y][x] = new Tree(new Point(x, y));
				break;
				case "c":
				field[y][x] = new Cookie(new Point(x, y));
				break;
				case "d":
				field[y][x] = new Candy(new Point(x, y));
				break;
			
			}
			}
			System.out.println(line);
			y++;
		
		}
		br.close();
		if (validField(field, y, x)) {
		gameLabel.setText("�i" + file.getName() + "�j���J���܂����D");
		return field;
		} else {
		gameLabel.setText("���[�h���s�I");
		System.out.println(x+" "+y+" "+field.length+" "+field[0].length);
	
	}
	} catch (FileNotFoundException e) {
	gameLabel.setText("���[�h���s�I");
	} catch (IOException e) {
	e.printStackTrace();
	gameLabel.setText("���[�h���s�I");
	}
	return null;

}
private boolean validField(Avatar[][] field, int y, int x) {
	// map size
	if (field.length > 0 && field.length != y) return false;
	if (field[0].length > 0 && field[0].length != x) return false;
	return true;

}
@Override
public void gameClear() {
	// TODO �����������ꂽ���\�b�h�E�X�^�u
	gameLabel.setText("�Q�[���N���A�I");
	timeline.stop();
}
@Override
public void gameOver() {
	// TODO �����������ꂽ���\�b�h�E�X�^�u
	gameLabel.setText("�Q�[���I�[�o�[�I");
	timeline.stop();
}
}