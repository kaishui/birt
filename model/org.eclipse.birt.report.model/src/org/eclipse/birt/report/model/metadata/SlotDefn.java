/*******************************************************************************
 * Copyright (c) 2004 Actuate Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *  Actuate Corporation  - initial API and implementation
 *******************************************************************************/

package org.eclipse.birt.report.model.metadata;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.birt.report.model.api.elements.ReportDesignConstants;
import org.eclipse.birt.report.model.api.metadata.IElementDefn;
import org.eclipse.birt.report.model.api.metadata.ISlotDefn;
import org.eclipse.birt.report.model.api.util.StringUtil;
import org.eclipse.birt.report.model.core.DesignElement;
import org.eclipse.birt.report.model.i18n.ModelMessages;
import org.eclipse.birt.report.model.validators.ISemanticTriggerDefnSetProvider;

/**
 * Meta-data about a slot within an element. Elements can act as a
 * <em>container</em>, that is one that can contain other elements. A
 * container has one or more <em>slots</em>. Many elements have just one
 * slot, but some (such as the design) have several. This class describes a
 * single slot.
 * <p>
 * A slot has the following attributes:
 * <p>
 * <li><strong>Name </strong>-- A display name to present to the user</li>
 * <li><strong>ID </strong>-- The internal identifier for the slot.</li>
 * <li><strong>Cardinality </strong>-- Whether the slot stores one item or a
 * list of items.</li>
 * <li><strong>Content Types </strong>-- The element type(s) that can appear in
 * the slot.</li>
 * </ul>
 * <li><strong>Content Types </strong>-- The element type(s) that can appear in
 * the slot.</li>
 * <p>
 * As with all meta-data objects, the set methods must be called only while
 * building the meta-data, before building the meta-data.
 * 
 */

public class SlotDefn implements ISlotDefn, ISemanticTriggerDefnSetProvider
{

	/**
	 * The slot ID. The ID is unique to an element type. In implemenation, it
	 * may give the position of the slot within an array of slots. Each element
	 * class defines the list of slot IDs for that element.
	 */

	private int slotID = 0;

	/**
	 * Slot cardinality. True if the cardinality is multiple, false if the
	 * cardinality is single.
	 */

	private boolean multipleCardinality = true;

	/**
	 * Internal name.
	 */

	private String name = null;

	/**
	 * The message ID for the display name for the slot.
	 */

	private String displayNameID = null;

	/**
	 * Pointers the the element meta data for the element types that the slot
	 * can hold.
	 */

	private ArrayList contentElements = new ArrayList( );

	/**
	 * List of one or more element type names that the slot can hold.
	 */

	private ArrayList contentTypes = new ArrayList( );

	/**
	 * Predefined style for elements in this slot. Null means that the slot does
	 * not define a predefined style.
	 */

	private String selector = null;

	/**
	 * The collection of semantic validation triggers.
	 */

	private SemanticTriggerDefnSet triggers = null;

	/**
	 * The BIRT release when this slot was introduced.
	 */

	private String since;

	/**
	 * The XML element name used to identify slot contents. Will be blank if the
	 * slot is anonymous.
	 */

	private String xmlName;

	/**
	 * Status whether name of the contents in this slot will be added to the
	 * namespace in module. True, add the name of the contents to namespace,
	 * otherwise not add names to namespace.
	 */

	private boolean isManagedByNameSpace = true;

	/**
	 * Returns the slot cardinality.
	 * 
	 * @return true if the cardinality is multiple, false if it is single
	 */

	public boolean isMultipleCardinality( )
	{
		return multipleCardinality;
	}

	/**
	 * Sets whether to allow multiple cardinality.
	 * 
	 * @param flag
	 *            The cardinality: multiple (true) or single (false).
	 */

	void setMultipleCardinality( boolean flag )
	{
		this.multipleCardinality = flag;
	}

	/**
	 * Returns the localized display name.
	 * 
	 * @return the display name
	 */

	public String getDisplayName( )
	{
		assert displayNameID != null;
		return ModelMessages.getMessage( this.displayNameID );
	}

	/**
	 * Returns the message ID for the display name.
	 * 
	 * @return the message ID for the display name
	 */

	public String getDisplayNameID( )
	{
		return displayNameID;
	}

	/**
	 * Sets the message ID for the display name.
	 * 
	 * @param msgID
	 *            the message ID to set
	 */

	void setDisplayNameID( String msgID )
	{
		displayNameID = msgID;
	}

	/**
	 * Returns the internal slot identifier.
	 * 
	 * @return the slot identifier
	 */

	public int getSlotID( )
	{
		return slotID;
	}

	/**
	 * Set the internal slot identifier.
	 * 
	 * @param id
	 *            the slot ID to set
	 */

	void setSlotID( int id )
	{
		slotID = id;
	}

	/**
	 * Returns the set of element types that can appear in the slot.
	 * 
	 * @return the list of content elements.
	 */

	public List getContentElements( )
	{
		return new ArrayList( contentElements );
	}

	/**
	 * Returns the set of element types that can appear in the slot. Extended
	 * elements are replaced by actual extensions.
	 * 
	 * @return the list of content elements.
	 */

	public List getContentExtendedElements( )
	{
		MetaDataDictionary dd = MetaDataDictionary.getInstance( );
		IElementDefn extendItem = dd
				.getElement( ReportDesignConstants.EXTENDED_ITEM );

		ArrayList contentsWithExtensions = new ArrayList( );
		contentsWithExtensions.addAll( contentElements );

		if ( contentElements.contains( extendItem ) )
		{
			contentsWithExtensions.remove( extendItem );

			for ( int i = 0; i < dd.getExtensions( ).size( ); i++ )
			{
				ExtensionElementDefn extension = (ExtensionElementDefn) dd
						.getExtensions( ).get( i );
				if ( PeerExtensionLoader.EXTENSION_POINT
						.equals( extension.extensionPoint ) )
					contentsWithExtensions.add( extension );
			}
		}
		return contentsWithExtensions;

	}

	/**
	 * Builds the semantic information for the slot.
	 * 
	 * @throws MetaDataException
	 *             if any violation occurs during build of the slot.
	 */

	protected void build( ) throws MetaDataException
	{
		if ( contentTypes.isEmpty( ) )
			throw new MetaDataException( new String[]{this.name},
					MetaDataException.DESIGN_EXCEPTION_MISSING_SLOT_TYPE );

		// The display name should not be provided, but the display name
		// ID must be provided.
		if ( displayNameID == null )
			throw new MetaDataException( new String[]{name},
					MetaDataException.DESIGN_EXCEPTION_MISSING_SLOT_NAME );

		// Translate the type names into element types.

		MetaDataDictionary dd = MetaDataDictionary.getInstance( );
		contentElements.clear( );
		Iterator iter = contentTypes.iterator( );
		while ( iter.hasNext( ) )
		{
			String name = (String) iter.next( );
			ElementDefn type = (ElementDefn) dd.getElement( name );
			if ( type == null )
				throw new MetaDataException( new String[]{this.name, name},
						MetaDataException.DESIGN_EXCEPTION_INVALID_SLOT_TYPE );
			contentElements.add( type );
		}

		getTriggerDefnSet( ).build( );
	}

	/**
	 * Determines if this slot can contain an element of the given type.
	 * 
	 * @param type
	 *            the type to test
	 * @return true if the slot can contain the type, false otherwise
	 */

	public boolean canContain( IElementDefn type )
	{
		Iterator iter = contentElements.iterator( );
		while ( iter.hasNext( ) )
		{
			ElementDefn element = (ElementDefn) iter.next( );
			if ( element.isKindOf( type ) )
				return true;
		}
		return false;
	}

	/**
	 * Determines if an element can reside within this slot.
	 * 
	 * @param content
	 *            the design element to check
	 * @return true if the element can reside in the slot, false otherwise
	 */

	public boolean canContain( DesignElement content )
	{
		return canContain( content.getDefn( ) );
	}

	/**
	 * Returns the internal name.
	 * 
	 * @return the name
	 */

	public String getName( )
	{
		return name;
	}

	/**
	 * Sets the internal name.
	 * 
	 * @param name
	 *            the name to set
	 */

	void setName( String name )
	{
		this.name = name;
	}

	/**
	 * Adds a content type.
	 * 
	 * @param type
	 *            the element type to add
	 */

	void addType( String type )
	{
		contentTypes.add( type );
	}

	/**
	 * Returns the default style name.
	 * 
	 * @return the default style name, or null if this slot does not define a
	 *         default style
	 */

	public String getDefaultStyle( )
	{
		// Default style is removed from rom.def, and we keep it for
		// compatiblity.

		return null;
	}

	/**
	 * Sets the predefined style name.
	 * 
	 * @param value
	 *            the predefined style name to set
	 */

	void setSelector( String value )
	{
		selector = value;
	}

	/**
	 * Returns the predefined style name.
	 * 
	 * @return the predefined style name, or null if this slot does not define a
	 *         predefined style
	 */

	public String getSelector( )
	{
		return selector;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.birt.report.model.metadata.ISemanticTriggerProvider#getTriggerDefnSet()
	 */

	public SemanticTriggerDefnSet getTriggerDefnSet( )
	{
		if ( triggers == null )
			triggers = new SemanticTriggerDefnSet( );

		return triggers;
	}

	/**
	 * Set the version in which the slot was introduced.
	 * 
	 * @param value
	 *            the version in which the slot was introduced
	 */

	public void setSince( String value )
	{
		since = value;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.birt.report.model.api.metadata.ISlotDefn#getSince()
	 */

	public String getSince( )
	{
		return since;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.birt.report.model.api.metadata.ISlotDefn#getXmlName()
	 */

	public String getXmlName( )
	{
		return xmlName;
	}

	/**
	 * Set the name of the XML element used to hold the slot.
	 * 
	 * @param value
	 *            the XML element name to set
	 */

	public void setXmlName( String value )
	{
		xmlName = value;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */

	public String toString( )
	{
		if ( !StringUtil.isBlank( getName( ) ) )
			return getName( );
		return super.toString( );
	}

	/**
	 * Returns the status whether to add the name of the contents in this slot
	 * to the namespace.
	 * 
	 * @return true if add the name of the contents to namespace, otherwise
	 *         false
	 */

	public boolean isManagedByNameSpace( )
	{
		return isManagedByNameSpace;
	}

	/**
	 * Sets the status whether to add the name of the contents in this slot to
	 * the namespace.
	 * 
	 * @param isManagedByNameSpace
	 *            true if add the name of the contents to the namespace,
	 *            otherwise false
	 */

	public void setManagedByNameSpace( boolean isManagedByNameSpace )
	{
		this.isManagedByNameSpace = isManagedByNameSpace;
	}

}
