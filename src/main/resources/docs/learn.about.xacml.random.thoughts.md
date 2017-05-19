# Why XACML
We want a flexible and standard solution to implements security constraints. XACML should fits our needs because is ABAC and security constraints are expressed with an xml file. It looks easy to translate high level specs in XACML... Going XACML I decide to explore the 3.0 (most recent) version. Sadly there are few projects that implements XACML 3.0. I choose Balana because is open-source and looks simple. Well I hope I don't get it wrong...

In XACML policy is the most important brick. You build your XACML requests with attributes and validate them against a policy. Policy are loaded in PDP (Policy Decision Point). You build your XACML request in a PEP (Policy Enforcement Point) and send them to PDP.

Policy is made by Rules and Target. Rules encapsulate Target too. A rule encapsulate also a Condition and an Effect.

Given a Request PDP gives you back a Response who can hold one of these four basic Decision values:
 * PERMITT (ok you can go),
 * DENY (you are not authorized),
 * INDETERMINATE (an error has occurred),
 * NOT_APPLICABLE (can't evaluate the request)

You pass values you want PDP to verify as Attributes in your RequestCtx's AttributeSet. Questions arises:
 * How to specify which request's attributes must match with which policy's attributes?
 * Can I use arbitrary ids about attributes?
 * How can achieve a DENY??

Given this policy:
```
<?xml version="1.0" encoding="UTF-8"?>
<Policy xmlns="urn:oasis:names:tc:xacml:3.0:core:schema:wd-17"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="urn:oasis:names:tc:xacml:3.0:core:schema:wd-17
http://docs.oasis-open.org/xacml/3.0/xacml-core-v3-schema-wd-17.xsd"
	PolicyId="urn:oasis:names:tc:xacml:3.0:example:SimplePolicy1"
	RuleCombiningAlgId="urn:oasis:names:tc:xacml:1.0:rule-combining-algorithm:deny-overrides"
	Version="1.0">
	<Description>
		Medi Corp access control policy
	</Description>
	<Target />
	<Rule RuleId="urn:oasis:names:tc:xacml:3.0:example:SimpleRule1"
		Effect="Permit">
		<Description>
			Any subject with an e-mail name in the med.example.com domain
			can perform any action on any resource.
		</Description>
		<Target>
			<AnyOf>
				<AllOf>
					<Match MatchId="urn:oasis:names:tc:xacml:1.0:function:rfc822Name-match">
						<AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">med.example.com</AttributeValue>
						<AttributeDesignator MustBePresent="false"
							Category="urn:oasis:names:tc:xacml:1.0:subject-category:access-subject"
							AttributeId="urn:oasis:names:tc:xacml:1.0:subject:subject-id"
							DataType="urn:oasis:names:tc:xacml:1.0:data-type:rfc822Name" />
					</Match>
				</AllOf>
			</AnyOf>
		</Target>
	</Rule>
</Policy>
```
It is easy to detect Effect as Rule's attribute. Target is a bit more complex. You have to provide an attribute who has "category" and "id" perfectly matching policy's target attribute's category and id. Otherwise target won't apply (it won't apply on requests who don't match). Target specify the set of requests the rule will apply on. So, once you specify which one will be verified it's time to tell what to verify.
Policies also have RuleCombiningAlgId attribute. RuleCombiningAlgId specify how to deal with multiple rules in the same policy.
```
<?xml version="1.0" encoding="UTF-8"?>
<Policy
xmlns="urn:oasis:names:tc:xacml:3.0:core:schema:wd-17"
xmlns:xacml ="urn:oasis:names:tc:xacml:3.0:core:schema:wd-17"
xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
xmlns:md="http://www.med.example.com/schemas/record.xsd"
PolicyId="urn:oasis:names:tc:xacml:3.0:example:policyid:1"
RuleCombiningAlgId="urn:oasis:names:tc:xacml:1.0:rule-combining-
algorithm:deny-overrides"
Version="1.0">
  <PolicyDefaults>
    <XPathVersion>http://www.w3.org/TR/1999/REC-xpath-19991116</XPathVersion>
  </PolicyDefaults>
  <Target/>
  <VariableDefinition VariableId="17590034">
    <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-equal">
      <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
        <AttributeDesignator
          MustBePresent="false"
          Category="urn:oasis:names:tc:xacml:1.0:subject-category:access-subject"
          AttributeId="urn:oasis:names:tc:xacml:3.0:example:attribute:patient-number"
          DataType="http://www.w3.org/2001/XMLSchema#string"/>
      </Apply>
      <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
        <AttributeSelector
          MustBePresent="false"
          Category="urn:oasis:names:tc:xacml:3.0:attribute-category:resource"
          Path="md:record/md:patient/md:patient-number/text()"
          DataType="http://www.w3.org/2001/XMLSchema#string"/>
        </Apply>
    </Apply>
  </VariableDefinition>
  <Rule RuleId="urn:oasis:names:tc:xacml:3.0:example:ruleid:1" Effect="Permit">
    <Description>
      A person may read any medical record in the
      http://www.med.example.com/schemas/record.xsd namespace
      for which he or she is the designated patient
    </Description>
    <Target>
      <AnyOf>
        <AllOf>
          <Match MatchId="urn:oasis:names:tc:xacml:1.0:function:anyURI-equal">
            <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#anyURI">urn:example:med:schemas:record</AttributeValue>
            <AttributeDesignator
              MustBePresent="false"
              Category="urn:oasis:names:tc:xacml:3.0:attribute-category:resource"
              AttributeId="urn:oasis:names:tc:xacml:2.0:resource:target-namespace"
              DataType="http://www.w3.org/2001/XMLSchema#anyURI"/>
          </Match>
          <Match MatchId="urn:oasis:names:tc:xacml:3.0:function:xpath-node-match">
            <AttributeValue DataType="urn:oasis:names:tc:xacml:3.0:data-type:xpathExpression"  XPathCategory="urn:oasis:names:tc:xacml:3.0:attribute-category:resource">
              md:record
            </AttributeValue>
            <AttributeDesignator
              MustBePresent="false"
              Category="urn:oasis:names:tc:xacml:3.0:attribute-category:resource"
              AttributeId="urn:oasis:names:tc:xacml:3.0:content-selector"
              DataType="urn:oasis:names:tc:xacml:3.0:data-type:xpathExpression"/>
          </Match>
        </AllOf>
      </AnyOf>
      <AnyOf>
        <AllOf>
          <Match MatchId="urn:oasis:names:tc:xacml:1.0:function:string-equal">
            <AttributeValue
              DataType="http://www.w3.org/2001/XMLSchema#string">
              read
            </AttributeValue>
            <AttributeDesignator
              MustBePresent="false"
              Category="urn:oasis:names:tc:xacml:3.0:attribute-category:action"
              AttributeId="urn:oasis:names:tc:xacml:1.0:action:action-id"
              DataType="http://www.w3.org/2001/XMLSchema#string"/>
          </Match>
        </AllOf>
      </AnyOf>
    </Target>
    <Condition>
      <VariableReference VariableId="17590034"/>
    </Condition>
  </Rule>
</Policy>
```
Tag Condition will do the job. Condition that won't match will determine corresponding Rule to beevaluated as INDETERMINATE (not DENY as I suppose).

AttributeDesignator indicates which attribute will be used to do evaluation by Target or Condition. AttributeSelector let you specify an XPath query to retreive value of the attribute (what?).

There must be perfect match between Condition attributes and contex attributes (basically what you put in request) about id and type or attributes will never match and their values will never been evaluated.

## Attributes
Attributes are what PDP evaluates to give you Response to your Request. The Attributes you specify goes in Bag in the context. Bag are set of attributes selected by type.
When you add an attribute in your Request you MUST specify _Category_, _AttributeId_, _Datatype_. _Issuer_ is not mandatory. PDP will retreive your attribute's value by these three mandatory attributes.

## Match
Cannot use Match function outside Target. To reproduce the same semantic you have to use a trick (my bad).

## What is Category?
Category is an AttributeDesignator's attribute. There are about 10 categories. I don't know what is category purpose. Maybe different kind of attributes are searched in different areas.

## Problems
I can't use in Condition the same attribute I use in Target. There is something I don't get right.. Let's see tomorrow.s
Still having trouble about retreiving attributes from context to evaluate against policy. That's insane..
Let's have this policy:
```
<?xml version="1.0" encoding="UTF-8"?>
<Policy xmlns="urn:oasis:names:tc:xacml:3.0:core:schema:wd-17"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="urn:oasis:names:tc:xacml:3.0:core:schema:wd-17
http://docs.oasis-open.org/xacml/3.0/xacml-core-v3-schema-wd-17.xsd"
	PolicyId="urn:oasis:names:tc:xacml:3.0:example:PolicyWithDENYRule"
	RuleCombiningAlgId="urn:oasis:names:tc:xacml:1.0:rule-combining-algorithm:first-applicable"
	Version="1.0">
	<Description>
		Medi Corp access control policy
	</Description>
	<Target />
	<Rule RuleId="urn:oasis:names:tc:xacml:3.0:example:OnlyJimShallPass"
		Effect="Permit">
		<Description>Any subject with an e-mail name in the med.example.com domain can perform any action on any resource.</Description>
		<Target>
			<AnyOf>
				<AllOf>
					<Match MatchId="urn:oasis:names:tc:xacml:1.0:function:rfc822Name-match">
						<AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">med.example.com</AttributeValue>
						<AttributeDesignator MustBePresent="false"
							Category="urn:oasis:names:tc:xacml:1.0:subject-category:access-subject"
							AttributeId="urn:oasis:names:tc:xacml:1.0:subject:subject-id"
							DataType="urn:oasis:names:tc:xacml:1.0:data-type:rfc822Name" />
					</Match>
				</AllOf>
			</AnyOf>
		</Target>
		<Condition>
			<Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:any-of">
				<Function FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-equal"/>
				<AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">jim@med.example.com</AttributeValue>
				<AttributeDesignator MustBePresent="false"
					Category="urn:oasis:names:tc:xacml:1.0:subject-category:access-subject"
					AttributeId="urn:oasis:names:tc:xacml:1.0:subject:subject-id"
					DataType="urn:oasis:names:tc:xacml:1.0:data-type:rfc822Name" />
			</Apply>
		</Condition>
	</Rule>
	<Rule Effect="Deny" RuleId="defaultRule" />
</Policy>
```
This policy doesn't work due to an illegal parameter: AttributeDesignator in Condition. What's the problem? Specification says I can use AttributeDesignator in Condition (because it is an Expression element).

Ok I solved it. Just change the policy's Condition this way:
```
<Condition>
	<Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:any-of">
		<Function FunctionId="urn:oasis:names:tc:xacml:1.0:function:rfc822Name-equal"/>
		<AttributeValue DataType="urn:oasis:names:tc:xacml:1.0:data-type:rfc822Name">jim@med.example.com</AttributeValue>
		<AttributeDesignator MustBePresent="false"
			Category="urn:oasis:names:tc:xacml:1.0:subject-category:access-subject"
			AttributeId="urn:oasis:names:tc:xacml:1.0:subject:subject-id"
			DataType="urn:oasis:names:tc:xacml:1.0:data-type:rfc822Name" />
	</Apply>
</Condition>
```
Because I want to check the rfc822Name against a value and value must be (of course) of the same type. So I change the function who checks values from `string-equal` to `rfc822Name-equal` and change the AttributeValue from `string` to `rfc822Name`.

# ISSUES
List of issues, problems or unaswered questions:
 * Can I change policies at runtime with Balana? Can I reload policy after it has been changed?
 * Where to store policies? Database would be better than a file.
 * Use Advices
 * Use Obbligations
