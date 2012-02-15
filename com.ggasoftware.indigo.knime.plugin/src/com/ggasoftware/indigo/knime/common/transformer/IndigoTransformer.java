package com.ggasoftware.indigo.knime.common.transformer;

import com.ggasoftware.indigo.IndigoObject;
/*
 * Interface for all indigo transformers
 */
public abstract class IndigoTransformer
{
   public abstract void transform (IndigoObject io, boolean reaction);
}