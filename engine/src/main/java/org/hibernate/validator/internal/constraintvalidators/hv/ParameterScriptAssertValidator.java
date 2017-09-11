/*
 * Hibernate Validator, declare and validate application constraints
 *
 * License: Apache License, Version 2.0
 * See the license.txt file in the root directory or <http://www.apache.org/licenses/LICENSE-2.0>.
 */
package org.hibernate.validator.internal.constraintvalidators.hv;

import static org.hibernate.validator.internal.util.CollectionHelper.newHashMap;
import static org.hibernate.validator.internal.util.logging.Messages.MESSAGES;

import java.util.List;
import java.util.Map;

import javax.validation.ConstraintValidatorContext;
import javax.validation.constraintvalidation.SupportedValidationTarget;
import javax.validation.constraintvalidation.ValidationTarget;

import org.hibernate.validator.constraints.ParameterScriptAssert;
import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorContext;
import org.hibernate.validator.internal.engine.constraintvalidation.ConstraintValidatorContextImpl;
import org.hibernate.validator.internal.engine.messageinterpolation.util.InterpolationHelper;
import org.hibernate.validator.internal.util.Contracts;

/**
 * Validator for the {@link ParameterScriptAssert} constraint annotation.
 *
 * @author Gunnar Morling
 * @author Guillaume Smet
 * @author Marko Bekhta
 */
@SupportedValidationTarget(ValidationTarget.PARAMETERS)
public class ParameterScriptAssertValidator extends AbstractScriptAssertValidator<ParameterScriptAssert, Object[]> {

	@Override
	public void initialize(ParameterScriptAssert constraintAnnotation) {
		validateParameters( constraintAnnotation );
		this.languageName = constraintAnnotation.lang();
		this.script = constraintAnnotation.script();
		this.escapedScript = InterpolationHelper.escapeMessageParameter( constraintAnnotation.script() );
	}

	@Override
	public boolean isValid(Object[] arguments, ConstraintValidatorContext constraintValidatorContext) {
		if ( constraintValidatorContext instanceof HibernateConstraintValidatorContext ) {
			constraintValidatorContext.unwrap( HibernateConstraintValidatorContext.class ).addMessageParameter( "script", escapedScript );
		}

		List<String> parameterNames = ( (ConstraintValidatorContextImpl) constraintValidatorContext )
				.getMethodParameterNames();

		Map<String, Object> bindings = getBindings( arguments, parameterNames );

		return getScriptAssertContext( constraintValidatorContext ).evaluateScriptAssertExpression( bindings );
	}

	private Map<String, Object> getBindings(Object[] arguments, List<String> parameterNames) {
		Map<String, Object> bindings = newHashMap();

		for ( int i = 0; i < arguments.length; i++ ) {
			bindings.put( parameterNames.get( i ), arguments[i] );
		}

		return bindings;
	}

	private void validateParameters(ParameterScriptAssert constraintAnnotation) {
		Contracts.assertNotEmpty( constraintAnnotation.script(), MESSAGES.parameterMustNotBeEmpty( "script" ) );
		Contracts.assertNotEmpty( constraintAnnotation.lang(), MESSAGES.parameterMustNotBeEmpty( "lang" ) );
	}
}
