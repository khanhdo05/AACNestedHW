import edu.grinnell.csc207.util.AssociativeArray;
import edu.grinnell.csc207.util.KeyNotFoundException;
import edu.grinnell.csc207.util.NullKeyException;

/**
 * Represents the mappings for a single category of items that should be displayed
 * 
 * @author Catie Baker & Khanh Do
 *
 */
public class AACCategory implements AACPage {
	/**
	 * The name of the category.
	 */
	private final String categoryName;

	/**
	 * The items in the category.
	 */
	private final AssociativeArray<String, String> items;

	/**
	 * Creates a new empty category with the given name
	 * 
	 * @param name the name of the category
	 */
	public AACCategory(String name) {
		this.categoryName = name;
		this.items = new AssociativeArray<>();
	} // AACCategory(String)

	/**
	 * Adds the image location, text pairing to the category
	 * 
	 * @param imageLoc the location of the image
	 * @param text the text that image should speak
	 *
	 * @throws NullKeyException if the image location is null
	 */
	@Override
	public void addItem(String imageLoc, String text) throws NullKeyException {
		this.items.set(imageLoc, text);
	} // addItem(String, String)

	/**
	 * Returns an array of all the images in the category
	 * 
	 * @return the array of image locations; if there are no images, it should return an empty array
	 */
	@Override
	public String[] getImageLocs() {
		return this.items.keys();
	} // getImageLocs()

	/**
	 * Returns the name of the category
	 * 
	 * @return the name of the category
	 */
	@Override
	public String getCategory() {
		return this.categoryName;
	} // getCategory()

	/**
	 * Returns the text associated with the given image in this category
	 * 
	 * @param imageLoc the location of the image
	 * @return the text associated with the image
	 * @throws KeyNotFoundException if the image provided is not in the current category
	 */
	@Override
	public String select(String imageLoc) throws KeyNotFoundException {
		return this.items.get(imageLoc);
	} // select(String)

	/**
	 * Determines if the provided images is stored in the category
	 * 
	 * @param imageLoc the location of the category
	 * @return true if it is in the category, false otherwise
	 */
	@Override
	public boolean hasImage(String imageLoc) {
		return this.items.hasKey(imageLoc);
	} // hasImage(String)
} // class AACCategory
