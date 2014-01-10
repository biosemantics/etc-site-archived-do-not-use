package edu.arizona.biosemantics.etcsite.client.content.matrixGeneration.review;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.CompositeCell;
import com.google.gwt.cell.client.EditTextCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.HasCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.Header;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.SimplePager.TextLocation;
import com.google.gwt.user.cellview.client.TextHeader;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SelectionChangeEvent.Handler;
import com.google.gwt.view.client.SingleSelectionModel;

import edu.arizona.biosemantics.etcsite.shared.rpc.matrixGeneration.Matrix;
import edu.arizona.biosemantics.etcsite.shared.rpc.matrixGeneration.Taxon;


public class ReviewView extends Composite implements IReviewView, Handler {

	private static ViewUiBinder uiBinder = GWT
			.create(ViewUiBinder.class);
	
	interface ViewUiBinder extends UiBinder<Widget, ReviewView> {
	}

	/**
	 * The main DataGrid.
	 */
	@UiField(provided = true)
	DataGrid<Taxon> dataGrid;

	/**
	 * The pager used to change the range of data.
	 */
	//@UiField(provided = true)
	//SimplePager pager;

	private Presenter presenter;
	
	private ListDataProvider<Taxon> dataProvider;
	private List<String> characterNames = new LinkedList<String>();
	private ProvidesKey<Taxon> taxonKeyProvider = new ProvidesKey<Taxon>() {
		@Override
		public Object getKey(Taxon item) {
			return item == null ? null : item.getId();
		}
	};
	private SingleSelectionModel<Taxon> selectionModel;
	
	private SimplePager createPager() {
	    SimplePager.Resources pagerResources = GWT.create(SimplePager.Resources.class);
	    SimplePager pager = new SimplePager(TextLocation.CENTER, pagerResources, false, 0, true);
		return pager;
	}

	private HandlerRegistration sortHandlerRegistration;
	
	public ReviewView() {
		dataProvider = new ListDataProvider<Taxon>();
		dataGrid = createDataGrid();
	    dataProvider.addDataDisplay(dataGrid);
		//pager = createPager();
		//pager.setDisplay(dataGrid);
		initWidget(uiBinder.createAndBindUi(this));
	}
	
	private DataGrid<Taxon> createDataGrid() {
		MyDataGridResource myDataGridResource = GWT.create(MyDataGridResource.class);
		DataGrid<Taxon> dataGrid = new DataGrid<Taxon>(50, myDataGridResource, taxonKeyProvider);
		dataGrid.setAutoHeaderRefreshDisabled(false);
		dataGrid.setAutoFooterRefreshDisabled(false);
		//buildDataGrid(dataGrid);
		return dataGrid;
	}

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}
	
	@UiHandler("saveButton")
	public void onSave(ClickEvent event) {
		presenter.onSave();
	}

	/**
	 * Add the columns to the table.
	 * @param dataGrid2 
	 */
	private void buildDataGrid(DataGrid<Taxon> dataGrid) {
		int columnCount = dataGrid.getColumnCount();
		for(int i=0; i < columnCount; i++) {
			dataGrid.removeColumn(0);
		}
		if(sortHandlerRegistration != null)
			sortHandlerRegistration.removeHandler();
		ListHandler<Taxon> sortHandler = new ListHandler<Taxon>(dataProvider.getList());
	    sortHandlerRegistration = dataGrid.addColumnSortHandler(sortHandler);
	    selectionModel = new SingleSelectionModel<Taxon>(taxonKeyProvider);
	    dataGrid.setSelectionModel(selectionModel);//, DefaultSelectionEventManager.<Taxon> createCheckboxManager());
		selectionModel.addSelectionChangeHandler(this);
	    
		//add first column taxon name
		List<HasCell<Taxon, ?>> nameCells = new LinkedList<HasCell<Taxon, ?>>();		
		
		DnDHandler dndHandlerTaxons = new TaxonDnDHandler(this);
		DnDImageCell dndImageCell = new DnDImageCell(dndHandlerTaxons) {
			@Override
		    public void render(Context context, String value, SafeHtmlBuilder sb) {
		        String imagePath = "images/move_vertical.png";
		        sb.appendHtmlConstant("<img src = '"+imagePath+"' class='reviewMatrixActionIcon' height = '27px' width = '10px' />");
		    }
		};
		Column<Taxon, String> dndImageColumn = new Column<Taxon, String>(dndImageCell) {
			@Override
			public String getValue(Taxon object) {
				return "";
			}
		};
		ClickHandler unusedCharactersClickHandler = new UnusedCharactersClickHandler(this);
		ClickImageCell clickImageCell = new ClickImageCell(unusedCharactersClickHandler) {
			@Override
		    public void render(Context context, String value, SafeHtmlBuilder sb) {
		        String imagePath = "images/curved_arrow.png";
		        sb.appendHtmlConstant("<img src = '"+imagePath+"' height = '15px' width = '20px' />");
		    }
		};
		Column<Taxon, String> unusedImageColumn = new Column<Taxon, String>(clickImageCell) {
			@Override
			public String getValue(Taxon object) {
				return "";
			}
		};
		Column<Taxon, String> nameColumn = new Column<Taxon, String>(
				new EditTextCell()) {
			@Override
			public String getValue(Taxon object) {
				return object.getName().replace("_", " ");
			}
		};
		nameColumn.setFieldUpdater(new FieldUpdater<Taxon, String>() {
			@Override
			public void update(int index, Taxon object,
					String value) {
				// Called when the user changes the value.
				object.setName(value);
			}
		});
		
		nameCells.add(dndImageColumn);
		nameCells.add(unusedImageColumn);
		nameCells.add(nameColumn);
		Cell<Taxon> nameCell = new CompositeCell<Taxon>(nameCells);
		Column<Taxon, Taxon> column = new Column<Taxon, Taxon>(nameCell) {
			@Override
			public Taxon getValue(Taxon object) {
				return object;
			}
		};
		column.setSortable(true);
		sortHandler.setComparator(column,
				new Comparator<Taxon>() {
					@Override
					public int compare(Taxon o1, Taxon o2) {
						return o1.getName().compareTo(o2.getName());
					}
				});
		TextHeader header = new TextHeader("Taxon Name");
		dataGrid.addColumn(column, header);
		
		int maxLength = 0;
		String maxName = "";
		for(Taxon taxon : dataProvider.getList()) {
			String state = taxon.getName();
			if(state.length() > maxLength) {
				maxLength = state.length();
				maxName = state;
			}
		}
		Label label = new Label(maxName);
		int maxWidth = label.getOffsetWidth();
		dataGrid.setColumnWidth(column, 100, Unit.PX);
		
		//add taxon charater state columns
		DnDHandler dndHandlerCharacters = new CharacterDnDHandler(this);
		for(final String characterName : characterNames) {
			final String displayCharacterName = characterName.replaceAll("_", " ");
			Column<Taxon, String> characterColumn = new Column<Taxon, String>(
					new EditTextCell()) {
				@Override
				public String getValue(Taxon object) {
					return object.getCharacterState(characterName);
				}
			};
			characterColumn.setSortable(true);
			sortHandler.setComparator(characterColumn,
					new Comparator<Taxon>() {
						@Override
						public int compare(Taxon o1, Taxon o2) {
							return o1.getCharacterState(displayCharacterName)
									.compareTo(o2.getCharacterState(displayCharacterName));
						}
					});
			
			List<HasCell<String, ?>> cellList = new LinkedList<HasCell<String, ?>>();
			DnDImageCell characterImageCell = new DnDImageCell(dndHandlerCharacters) {
				@Override
			    public void render(Context context, String value, SafeHtmlBuilder sb) {
			        String imagePath = "images/move_horizontal.png";
			        sb.appendHtmlConstant("<img src = '"+imagePath+"' height = '10px' width = '27px' />");
			    }
			};
			Column<String, String> characterImageColumn = new Column<String, String>(characterImageCell) {
				@Override
				public String getValue(String object) {
					return object;
				}
			};
			cellList.add(characterImageColumn);
			Column<String, String> characterNameColumn = new Column<String, String>(new EditTextCell()) {
				@Override
				public String getValue(String object) {
					return object;
				}
			};
			cellList.add(characterNameColumn);
			
			DnDHeaderCell cell = new DnDHeaderCell(cellList); 
			DnDHeader dndHeader = new DnDHeader(displayCharacterName, cell);
			dataGrid.addColumn(characterColumn, dndHeader);
			characterColumn.setFieldUpdater(new FieldUpdater<Taxon, String>() {
						@Override
						public void update(int index, Taxon object,
								String value) {
							// Called when the user changes the value.
							object.setCharacterState(characterName, value);
						}
					});
			
			maxLength = 0;
			String maxState = "";
			for(Taxon taxon : dataProvider.getList()) {
				String state = taxon.getCharacterState(characterName);
				if(state.length() > maxLength) {
					maxLength = state.length();
					maxState = state;
				}
			}
			label = new Label(maxState);
			maxWidth = label.getOffsetWidth();
			dataGrid.setColumnWidth(characterColumn, 100, Unit.PX);
		}
	}
	
	@Override
	public void setMatrix(Matrix matrix) {
		this.characterNames = matrix.getCharacterNames();
		List<Taxon> taxonList = dataProvider.getList();
		taxonList.clear();
		taxonList.addAll(matrix.getTaxons());
		//TODO refresh scrollpanel here somehow so it knows about changing width; e.g. set a longer charactername list and it will overflow, not visible parts then
		this.buildDataGrid(dataGrid);
	}

	@Override
	public void onSelectionChange(SelectionChangeEvent event) {
		
	}

	@Override
	public void updateTaxon(Taxon taxon) {
		List<Taxon> Taxons = dataProvider.getList();
		Iterator<Taxon> TaxonsIterator = Taxons.iterator();
		
		boolean found = false;
		while(TaxonsIterator.hasNext()) {
			Taxon listTaxon = TaxonsIterator.next();
			if(listTaxon.getId()==taxon.getId()) {
				TaxonsIterator.remove();
				found = true;
			}
		}
		if(found)
			Taxons.add(taxon);
	}

	@Override
	public void removeTaxon(Taxon taxon) {
		List<Taxon> taxons = dataProvider.getList();
		Iterator<Taxon> TaxonsIterator = taxons.iterator();
		while(TaxonsIterator.hasNext()) {
			Taxon listTaxon = TaxonsIterator.next();
			if(listTaxon.getId()==taxon.getId()) {
				TaxonsIterator.remove();
			}
		}
	}
	

	@Override
	public void addTaxon(Taxon taxon) {
		List<Taxon> taxons = dataProvider.getList();
		taxons.add(taxon);
	}
	
	public void swapTaxons(int rowIdA, int rowIdB) {
		List<Taxon> taxons = dataProvider.getList();
		Collections.swap(taxons, rowIdA, rowIdB);
	}
	
	public void swapCharacters(int characterIdA, int characterIdB) {
		int columnIdA = characterIdA + 1;
		int columnIdB = characterIdB + 1;
		Collections.swap(this.characterNames, characterIdA, characterIdB);
		Column<Taxon, ?> sourceColumn = dataGrid.getColumn(columnIdA);
		Header<?> sourceHeader = dataGrid.getHeader(columnIdA);
		dataGrid.removeColumn(columnIdA);
		if(columnIdA >= columnIdB) {
			dataGrid.insertColumn(columnIdB, sourceColumn, sourceHeader);
		} else {
			dataGrid.insertColumn(columnIdB - 1, sourceColumn, sourceHeader);
		}
		//buildDataGrid(dataGrid);
	}
	
	public void moveTaxon(int movingTaxon, int lowerNeighbor) {
		List<Taxon> taxons = dataProvider.getList();
		int distance = lowerNeighbor - movingTaxon;
		Taxon moving = taxons.get(movingTaxon);
		if(distance > 0) {
			taxons.add(lowerNeighbor, moving);
			taxons.remove(moving);
		} else if(distance < 0) {
			taxons.remove(moving);
			taxons.add(lowerNeighbor, moving);
		}
		
		/*if(distance > 0) 
			Collections.rotate(taxons.subList(movingTaxon, lowerNeighbor + 1), -distance);
		else if(distance < 0)
			Collections.rotate(taxons.subList(movingTaxon, lowerNeighbor + 1), distance);*/
	}
	
	public void moveCharacter(int characterIdA, int characterIdB) {
		int columnIdA = characterIdA + 1;
		int columnIdB = characterIdB + 1;
		Column<Taxon, ?> sourceColumn = dataGrid.getColumn(columnIdA);
		Header<?> sourceHeader = dataGrid.getHeader(columnIdA);
		dataGrid.removeColumn(columnIdA);
		String character = characterNames.get(characterIdA);
		characterNames.remove(characterIdA);
		
		if(columnIdA >= columnIdB) {
			dataGrid.insertColumn(columnIdB, sourceColumn, sourceHeader);
			characterNames.add(characterIdB, character);
		} else {
			dataGrid.insertColumn(columnIdB - 1, sourceColumn, sourceHeader);
			characterNames.add(characterIdB - 1, character);
		}
		/*moveCharacterData(movingCharacter, lowerNeighbor);
		buildDataGrid(dataGrid); */
	}
	
	private void moveCharacterData(int columnIdA, int columnIdB) {
		/*int distance = lowerNeighbor - movingCharacter;
		String character = characterNames.get(movingCharacter);
		if(distance > 0) {
			characterNames.add(lowerNeighbor, character);
			characterNames.remove(character);
		} else if(distance < 0) {
			characterNames.remove(character);
			characterNames.add(lowerNeighbor, character);
		}*/
		
		
		
		/*if(distance > 0) 
		Collections.rotate(characterNames.subList(movingCharacter, lowerNeighbor + 1), -distance);
			else if(distance < 0)
		Collections.rotate(characterNames.subList(movingCharacter, lowerNeighbor + 1), distance);*/
	}

	public void orderCharactersForTaxon(int taxonId) {
		List<Taxon> taxons = dataProvider.getList();
		Taxon taxon = taxons.get(taxonId);
		int insertPosition = characterNames.size();
		boolean nonEmptyCharacterStateFound = false;
		for(int i = this.characterNames.size() - 1 ; i >= 0; i--) {
			String character = this.characterNames.get(i);
			String state = taxon.getCharacterState(character);
			if(state.isEmpty() && i != insertPosition) {
				if(!nonEmptyCharacterStateFound)
					insertPosition--;
				this.moveCharacter(i, insertPosition);
			} else {
				nonEmptyCharacterStateFound = true;
			}
		}
		//buildDataGrid(dataGrid);
	}

	@Override
	public void resetSelection() {
		this.selectionModel.clear();
	}


	@Override
	public Taxon getSelectedTaxon() {
		return selectionModel.getSelectedObject();
	}

	@Override
	public Matrix getMatrix() {
		Matrix matrix = new Matrix();
		//dataprovider gives a non-serializable list implementation
		List<Taxon> taxons = new LinkedList<Taxon>();
		taxons.addAll(dataProvider.getList());
		matrix.setTaxons(taxons);
		matrix.setCharacterNames(characterNames);
		return matrix;
	}

}
