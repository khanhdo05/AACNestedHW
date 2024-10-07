import edu.grinnell.csc207.util.AssociativeArray;
import edu.grinnell.csc207.util.KeyNotFoundException;
import edu.grinnell.csc207.util.NullKeyException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.NoSuchElementException;

/**
 * Creates a set of mappings of an AAC that has two levels, one for categories and then within each
 * category, it has images that have associated text to be spoken. This class provides the methods
 * for interacting with the categories and updating the set of images that would be shown and
 * handling an interactions.
 * 
 * @author Catie Baker & Khanh Do
 *
 */
public class AACMappings implements AACPage {
	private final String defaultCategory = "img/home.png";
	private String currentCategory = defaultCategory;
	private final AssociativeArray<String, AACCategory> categories;
	private final AssociativeArray<String, String> filesToNames;

	/**
	 * Creates a set of mappings for the AAC based on the provided file. The file is read in to create
	 * categories and fill each of the categories with initial items. The file is formatted as the
	 * text location of the category followed by the text name of the category and then one line per
	 * item in the category that starts with > and then has the file name and text of that image
	 * 
	 * for instance: img/food/plate.png food >img/food/icons8-french-fries-96.png french fries
	 * >img/food/icons8-watermelon-96.png watermelon img/clothing/hanger.png clothing
	 * >img/clothing/collaredshirt.png collared shirt
	 * 
	 * represents the file with two categories, food and clothing and food has french fries and
	 * watermelon and clothing has a collared shirt
	 * 
	 * @param filename the name of the file that stores the mapping information
	 */
	public AACMappings(String filename) {
		this.categories = new AssociativeArray<>();
		this.filesToNames = new AssociativeArray<>();
		this.loadfile(filename);
	} // AACMappings(String)

	/**
	 * Loads the file and creates the categories and items based on the information in the file
	 *
	 * @param filename the name of the file that stores the mapping information
	 */
	private void loadfile(String filename) {
		try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
			String line;
			while ((line = br.readLine()) != null) {
				String[] args = line.split(" ");
				// Making sure text is one string
				if (args.length > 2) {
					for (int i = 2; i < args.length; i++) {
						args[1] += " " + args[i];
					} // for
				} // if
				if (!line.startsWith(">")) {
					this.currentCategory = defaultCategory;
					this.filesToNames.set(args[0], args[1]);
				} else {
					args[0] = args[0].substring(1);
				} // if/else
				this.addItem(args[0], args[1]);
			} // while
		} catch (Exception e) {
			// Do nothing?
		} // try/catch
		reset();
	} // loadfile(String)

	/**
	 * Given the image location selected, it determines the action to be taken. This can be updating
	 * the information that should be displayed or returning text to be spoken. If the image provided
	 * is a category, it updates the AAC's current category to be the category associated with that
	 * image and returns the empty string. If the AAC is currently in a category and the image
	 * provided is in that category, it returns the text to be spoken.
	 * 
	 * @param imageLoc the location where the image is stored
	 * @return if there is text to be spoken, it returns that information, otherwise it returns the
	 *         empty string
	 * @throws NoSuchElementException if the image provided is not in the current category
	 */
	@Override
	public String select(String imageLoc) throws NoSuchElementException {
		// If the image provided is a category, it updates the AAC's current category to be the category
		// associated with that image and returns the empty string
		if (this.currentCategory.equals(this.defaultCategory)) {
			if (this.categories.hasKey(imageLoc)) {
				this.currentCategory = imageLoc;
				return "";
			} // if
			throw new NoSuchElementException("The image provided is not in the home screen");
		} else {
			// If the AAC is currently in a category and the image provided is in that category, it
			// returns
			// the text to be spoken
			try {
				return (this.categories.get(this.currentCategory)).select(imageLoc);
			} catch (KeyNotFoundException e) {
				throw new NoSuchElementException("The image provided is not in the current category");
			} // try/catch
		} // if/else
	} // select(String)

	/**
	 * Provides an array of all the images in the current category
	 * 
	 * @return the array of images in the current category; if there are no images, it should return
	 *         an empty array
	 */
	@Override
	public String[] getImageLocs() {
		try {
			if (this.currentCategory.equals(this.defaultCategory)) {
				return this.categories.keys();
			} // if
			return (this.categories.get(this.currentCategory)).getImageLocs();
		} catch (KeyNotFoundException e) {
			// If there's no current category, also return an empty array
			return new String[0];
		} // try/catch
	} // getImageLocs()

	/**
	 * Resets the current category of the AAC back to the default category
	 */
	public void reset() {
		this.currentCategory = this.defaultCategory;
	} // reset()


	/**
	 * Writes the ACC mappings stored to a file. The file is formatted as the text location of the
	 * category followed by the text name of the category and then one line per item in the category
	 * that starts with > and then has the file name and text of that image
	 * 
	 * for instance: img/food/plate.png food >img/food/icons8-french-fries-96.png french fries
	 * >img/food/icons8-watermelon-96.png watermelon img/clothing/hanger.png clothing
	 * >img/clothing/collaredshirt.png collared shirt
	 * 
	 * represents the file with two categories, food and clothing and food has french fries and
	 * watermelon and clothing has a collared shirt
	 * 
	 * @param filename the name of the file to write the AAC mapping to
	 */
	public void writeToFile(String filename) {
		// Try-with-resources to automatically close the writer
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
			// Iterate through the categories
			for (String category : this.getImageLocs()) {
				// Getting the category object at that category file path
				AACCategory categoryObject = this.categories.get(category);
				// Getting the category name
				String categoryName = categoryObject.getCategory();
				// Write the category file path and the category name
				writer.write(category + " " + categoryName + " ");
				for (String icon : categoryObject.getImageLocs()) {
					// Write the icon file path and the icon name
					writer.newLine();
					writer.write(">" + icon + " " + categoryObject.select(icon));
				} // for
				writer.newLine();
			} // for
		} catch (Exception e) {
			// Handle potential exceptions
		}
	} // writeToFile(String)

	/**
	 * Adds the mapping to the current category (or the default category if that is the current
	 * category)
	 * 
	 * @param imageLoc the location of the image
	 * @param text the text associated with the image
	 */
	@Override
	public void addItem(String imageLoc, String text) {
		try {
			// If the we're on the homescreen, create a new category with given imageLoc and name text
			if (this.currentCategory.equals(this.defaultCategory)) {
				this.categories.set(imageLoc, new AACCategory(text));
				this.currentCategory = imageLoc;
			} else {
				// If we're in a category, add the imageLoc and text to the current category
				AACCategory category = this.categories.get(this.currentCategory);
				category.addItem(imageLoc, text);
			} // if/else
		} catch (NullKeyException | KeyNotFoundException e) {
			// Do nothing
		} // try/catch
	} // addItem(String, String)

	/**
	 * Gets the name of the current category
	 * 
	 * @return returns the current category or the empty string if on the default category
	 */
	@Override
	public String getCategory() {
		try {
			return this.filesToNames.get(this.currentCategory);
		} catch (KeyNotFoundException e) {
			return "";
		} // try/catch
	} // getCategory()


	/**
	 * Determines if the provided image is in the set of images that can be displayed and false
	 * otherwise
	 * 
	 * @param imageLoc the location of the category
	 * @return true if it is in the set of images that can be displayed, false otherwise
	 */
	@Override
	public boolean hasImage(String imageLoc) {
		try {
			if (this.currentCategory.equals(this.defaultCategory)) {
				return this.categories.hasKey(imageLoc);
			} // if
			return (this.categories.get(this.currentCategory)).hasImage(imageLoc);
		} catch (KeyNotFoundException e) {
			// If there's no current category, return false
			return false;
		} // try/catch
	} // hasImage(String)
}
