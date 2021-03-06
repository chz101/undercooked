/*
 * Tile is the java class that has interactions with the user and items.
 * Every tile has its own strategy, which dictates how it functions.
 * The player interacts with the tile by using the X and C keys
 *
*/
import java.awt.event.KeyEvent;
import java.awt.Graphics;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.awt.Point;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.util.List;

public class Tile{
	private int[] position;
	private int type;
	private int standable;
	private String dispenserDesc; //If dispenser, item's description.
	private List<String> items;
	private Item item;
	private Strategy strategy;
	private Store store;
	private BufferedImage image;
	private BufferedImage itemimage;
	private Point point;

	//Tile Creation. Depending on the type, each tile is given a specific strategy.
	public Tile(int type, int x, int y){
		this.type = type;
		if (type == 0)
			strategy = new Floor();
		if (type == 2)
			strategy = new CuttingBoard();
		if (type == 3)
			strategy = new Grill();
		if (type == 4)
			strategy = new GarbageDisposal();
		if (type == 6)
			strategy = new Counter();
		position = new int[2];
		position[0] = x;
		position[1] = y;
		point = new Point(y,x);
		loadImage();

	}

	//If the tile should be a dispenser type, also need the description of what it dispenses.
	public Tile(int type, int x, int y,String desc){ //dispenser will be type 1
		this.type = type;
		this.dispenserDesc = desc;
		this.item = new Item(desc);
		strategy = new Dispenser();
		point = new Point(y, x);
		loadImage();
	}

	//Assembler constructor
	public Tile(int type, int x, int y, Store store, List<String> holding){
		this.items = holding;
		this.type = type;
		this.store = store;
		strategy = new Assembler();
		point = new Point(y,x);
		loadImage();
	}

	//Strategy Pattern functions
	public void action(){
		strategy.action(this);
	}

	public void update(){
		strategy.update(this);
	}

	//Check if the tile is empty and can hold an item. If not and the player is holding something, swap the items.
	public void holdingPlace(Player player) {
		if(item != null){
			strategy.swap(this, player);
		}
		else{
			strategy.place(this, player);
		}
	}

	//If the player is not holding anything, try to take an item from the tile.
	public void emptyPlace(Player player){
		if(item != null){
			//System.out.println("Picking up a " + this.getItem().getName());
			//System.out.println("Cut lvl: " + this.getItem().getCut());
			strategy.pickup(this, player);
		}
	}

	//Server the order
	public void orderUp(){
		store.serveOrder(items);
		this.items.clear();
	}

	//load the image of the tile when requested.
	private void loadImage(){
		strategy.loadImage(this);
	}


	//-----------------------------------
	//Setters
	public int setItem(Item i){ //1 on success, 0 on failure
		if (i == null){
			item = null;
			return 1;
		}
		if (item == null){
			item = i;
			return 1;
		}
		return 0;
	}

	public void setImage(BufferedImage image){
		this.image = image;
	}

	//Draw the tile image at the correct coordinate location.
	public void draw(Graphics g, ImageObserver observer){
		loadImage();
		g.drawImage(image, point.x * Store.TILE_SIZE, point.y*Store.TILE_SIZE, observer);
		if (item != null){
			loadItemImage();
			g.drawImage(itemimage, point.x * Store.TILE_SIZE, point.y*Store.TILE_SIZE, observer);
		}
	}

	//Draw the item image on top of the tile, if there is one.
	private void loadItemImage(){
		String itemName = item.getName();
		int cooklvl = item.getCook();
		int cutlvl  = item.getCut();

	    String cut = "";
	    String cook= "";
	    if(cutlvl > 0)
		    cut = "_cut";
	    if(cooklvl == 0)
		    cook = "_raw";
	    else if(cooklvl == 1)
		    cook = "_cooked";
	    else if(cooklvl == 2)
		    cook = "_burnt";
	    try{
		itemimage = ImageIO.read(new File("resources/" + itemName + cut + cook +".png"));
		} catch (IOException exc){
			System.out.println("Error opening image file for " + itemName + exc.getMessage());
		}

	}

	public void setPosition(int x,int y){
		position[0] = x;
		position[1] = y;
	}

	public void assembleItem(Item item){
		items.add(item.getName() +","+ item.getCut() +","+ item.getCook());
	}
	
	//Getters
	public Item getItem(){
		return item;
	}
	public int[] getPosition(){
		return position;
	}
	public Point getPoint(){
		return point;
	}

	public int getType(){
		return type;
	}
	public String getDispDesc(){
		return dispenserDesc;
	}
	public List<String> getItems(){
		return items;
	}
}
