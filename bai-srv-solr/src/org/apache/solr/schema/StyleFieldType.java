package org.apache.solr.schema;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.lucene.document.Fieldable;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.FieldCache;
import org.apache.lucene.search.FieldComparator;
import org.apache.lucene.search.FieldComparatorSource;
import org.apache.lucene.search.SortField;
import org.apache.solr.response.TextResponseWriter;
import org.apache.solr.response.XMLWriter;
import org.apache.solr.schema.FieldType;
import org.apache.solr.schema.SchemaField;

public class StyleFieldType extends FieldType{

	@Override
	public void write(XMLWriter xmlWriter, String name, Fieldable f)
			throws IOException {
		xmlWriter.writeStr(name, f.stringValue());
		
	}

	@Override
	public void write(TextResponseWriter writer, String name, Fieldable f)
			throws IOException {
		writer.writeStr(name, f.stringValue(), false);
		
	}

	@Override
	public SortField getSortField(SchemaField field, boolean top) {
		return new SortField(field.getName(), new StyleSort());
	}
	
	public static class StyleSort extends FieldComparatorSource {

		private static final long serialVersionUID = 1L;

		@Override
		public FieldComparator<?> newComparator(String fieldname, int numHits,
				int sortPos, boolean reversed) throws IOException {
			return new StyleComparator(numHits);
		}

	}

	@SuppressWarnings("rawtypes")
	public static class StyleComparator extends FieldComparator {
		// field tid,type,sales
		long[] ids;
		int[] types;
		long[] sales;

		long[] xids;
		int[] xtypes;
		long[] xsales;

		long bt_id;
		int bt_type;
		long bt_sales;

		ArrayList<Long> tids = new ArrayList<Long>();

		public StyleComparator(int numHits) {
			ids = new long[numHits];
			types = new int[numHits];
			sales = new long[numHits];
		}

		@Override
		public int compare(int slot1, int slot2) {
			int type1 = types[slot1];
			int type2 = types[slot2];
			long id1 = ids[slot1];
			long id2 = ids[slot2];
			long sales1 = sales[slot1];
			long sales2 = sales[slot2];

			if(type1 == type2){
				if(type1 == 1) {
					if(sales2 >= sales1)
						tids.add(id2);
					else
						tids.add(id1);
				}
				else {
					if(tids.contains(id1)) {
						if( tids.contains(id2)) {
							return tids.indexOf(id1) - tids.indexOf(id2);
						}
						return -1;
					}
					if(tids.contains(id2)) {
						return 1;
					}
				}
				return (int) (sales2 - sales1);
			}
			
			if(tids.contains(id1)) {
				if(tids.contains(id2)) {
					return tids.indexOf(id1) - tids.indexOf(id2);
				}
				return -1;
			}
			if(tids.contains(id2)) {
				return 1;
			}
			
			if(type1 == 1)
				tids.add(id1);
			else if(type2 == 1)
				tids.add(id2);
			
			return type1 - type2;
		}

		

		@Override
		public void setBottom(int slot) {
			this.bt_id = ids[slot];
			this.bt_type = types[slot];
			this.bt_sales = sales[slot];
		}

		@Override
		public int compareBottom(int doc) throws IOException {
			final int type2 = xtypes[doc];
			final long id2 = xids[doc];
			final long sales2 = xsales[doc];

			if(bt_type == type2){
				if(bt_type == 1) {
					if(sales2 >= bt_sales)
						tids.add(id2);
					else
						tids.add(bt_id);
				}
				else {
					if(tids.contains(bt_id)) {
						if( tids.contains(id2)) {
							return tids.indexOf(bt_id) - tids.indexOf(id2);
						}
						return -1;
					}
					if(tids.contains(id2)) {
						return 1;
					}
				}
				return (int) (sales2 - bt_sales);
			}
			
			if(tids.contains(bt_id)) {
				if(tids.contains(id2)) {
					return tids.indexOf(bt_id) - tids.indexOf(id2);
				}
				return -1;
			}
			if(tids.contains(id2)) {
				return 1;
			}
			
			if(bt_type == 1)
				tids.add(bt_id);
			else if(type2 == 1)
				tids.add(id2);
			
			return bt_type - type2;
		}

		@Override
		public void copy(int slot, int doc) throws IOException {
			this.ids[slot] = this.xids[doc];
			this.types[slot] = this.xtypes[doc];
			this.sales[slot] = this.xsales[doc];

		}

		@Override
		public void setNextReader(IndexReader reader, int docBase)
				throws IOException {
			this.xids = FieldCache.DEFAULT.getLongs(reader, "tid");
			this.xtypes = FieldCache.DEFAULT.getInts(reader, "type");
			this.xsales = FieldCache.DEFAULT.getLongs(reader, "sales");
		}

		@Override
		public Object value(int slot) {
			return null;
		}

	}

}
