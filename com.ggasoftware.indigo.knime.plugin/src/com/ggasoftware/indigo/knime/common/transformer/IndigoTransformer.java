package com.ggasoftware.indigo.knime.common.transformer;

import com.ggasoftware.indigo.IndigoObject;
/*
 * Interface for all indigo transformers
 */
public interface IndigoTransformer
{
   public void transform (IndigoObject io, boolean reaction);
}