package com.storageroomapp.client;

/**
 * Encapsulates a Collection query for Entries on the 
 * StorageRoom API. Given the complexity and power of
 * the SR API in this area, no attempt was made to make this 
 * a real abstraction.
 * <p>
 * The members you can set in this class are raw values
 * sent to the SR API on the URL.
 *
 */
public class CollectionQuery {

	// Query Options
	
	/**
	 * Identifier of the field that governs the sort order
	 * of the filter. Set to null if no sort order is needed.
	 */
	public String sortFieldName = null;

	/**
	 * The sort direction (ASC, DESC, or NONE)
	 */
	public SortOrder sortOrder = SortOrder.NONE;
	public enum SortOrder {
		NONE,
		ASC,
		DESC
	}
	
	/**
	 * Page size of results to bring back from the server.
	 * Use -1 to use the default page size.
	 */
	public int pageSize = -1;

	
	// Filter Options
	/** 
	 * Filter string, or set to null to retrieve all rows.
	 * 
	 * Because StorageRoom has a very rich filtering API,
	 * no attempt in this version of the Java API is made
	 * to abstract the filtering capabilities. Pass in the
	 * raw filtering string as per the docs at:
	 *   http://storageroomapp.com/documentation#search
	 * 
	 * Examples of this String include:
	 *   "price!gt=10&price!lt=50"
	 *   "@trash=true"
	 *   "location!within=((49.12,9.07),1)"
	 *   "description!match=/\btest\b/"
	 */
	public String filterOptions = null;
	
	/**
	 * Generate the query string for the URL given the query
	 * and filter options configured.
	 */
	protected String generateQueryString(int page) {
		StringBuilder sb = new StringBuilder();
		
		boolean needAmpersand = false;
		if (filterOptions != null) {
			sb.append(filterOptions);
			needAmpersand = true;
		}
		if (pageSize != -1) {
			if (needAmpersand) {
				sb.append("&");
			}
			sb.append("per_page=");
			sb.append(pageSize);
			needAmpersand = true;
		}
		if (sortOrder != SortOrder.NONE) {
			if (needAmpersand) {
				sb.append("&");
			}
			sb.append("order=");
			if (sortOrder == SortOrder.ASC) {
				sb.append("asc");
			} else {
				sb.append("desc");
			}
			needAmpersand = true;
		}
		if (sortFieldName != null) {
			if (needAmpersand) {
				sb.append("&");
			}
			sb.append("sort=");
			sb.append(sortFieldName);
			needAmpersand = true;
		}
		if (page > 0) {
			if (needAmpersand) {
				sb.append("&");
			}
			sb.append("page=");
			sb.append(page);
			needAmpersand = true;
		}
		
		return sb.toString();
	}
}
