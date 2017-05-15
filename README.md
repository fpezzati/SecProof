# Math
\\[ x = {-b \pm \sqrt{b^2-4ac} \over 2a} \\]

# Fine grained security with XACML
Ok I start with XACML. I will use version 3.0. Not sure about who is implementing this version, it seems most famous ones (JBoss for example) stick to 2.0 version.
XACML consist in PEP, PDP and policies.
 * PEP (Policy Enforcement Point) is where one or more policies condition must be met,
 * PDP (Policy Decision Point) is where attributes are evaluated by conditions,
 * policies are conditions, requirements you must met to be able to do something.

## Writing PEP
PEP is where authorization check should be triggered.

## Ok I go for Balana
Why? Well is the first XACML framework who provides it's dependencies in an easy way... But can't find docs...

org.wso2.balana.ctx.xacml3.RequestCtx should be XACML 3.0 request implementation. It's weird that Balana PDP evaluates requests as string.

## PDP and how to configure them
Balana needs some conf to be in idle

'''
<?xml version="1.0" encoding="UTF-8"?>
<config defaultPDP="pdp" defaultAttributeFactory="attr"
	defaultCombiningAlgFactory="comb" defaultFunctionFactory="func">
	<pdp name="pdp">
		<attributeFinderModule class="org.wso2.balana.finder.impl.CurrentEnvModule" />
		<attributeFinderModule class="org.wso2.balana.finder.impl.SelectorModule" />
	</pdp>
	<attributeFactory name="attr" useStandardDatatypes="true" />
	<functionFactory name="func" useStandardFunctions="true" />
	<combiningAlgFactory name="comb"
		useStandardAlgorithms="true">
		<algorithm class="org.wso2.balana.samples.custom.algo.HighestEffectRuleAlg" />
	</combiningAlgFactory>
</config>
'''

this config can be used to instantiate the "pdp" PDP... For what?

### Policy example
XACML does not look easy.

'''
<Policy xmlns="urn:oasis:names:tc:xacml:3.0:core:schema:wd-17"  PolicyId="sample" RuleCombiningAlgId="urn:oasis:names:tc:xacml:1.0:rule-combining-algorithm:first-applicable" Version="1.0">
   <Description>sample policy</Description>
   <Target></Target>
   <Rule Effect="Permit" RuleId="primary-group-customer-rule">
      <Target>
         <AnyOf>
            <AllOf>
               <Match MatchId="urn:oasis:names:tc:xacml:1.0:function:string-regexp-match">
                  <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">http://localhost:8280/services/Customers/getCustomers</AttributeValue>
                  <AttributeDesignator AttributeId="urn:oasis:names:tc:xacml:1.0:resource:resource-id" Category="urn:oasis:names:tc:xacml:3.0:attribute-category:resource" DataType="http://www.w3.org/2001/XMLSchema#string" MustBePresent="true"></AttributeDesignator>
               </Match>
               <Match MatchId="urn:oasis:names:tc:xacml:1.0:function:string-equal">
                  <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">read</AttributeValue>
                  <AttributeDesignator AttributeId="urn:oasis:names:tc:xacml:1.0:action:action-id" Category="urn:oasis:names:tc:xacml:3.0:attribute-category:action" DataType="http://www.w3.org/2001/XMLSchema#string" MustBePresent="true"></AttributeDesignator>
               </Match>
            </AllOf>
         </AnyOf>
      </Target>
      <Condition>
         <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-equal">
            <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
               <AttributeDesignator AttributeId="group" Category="urn:oasis:names:tc:xacml:3.0:example-group" DataType="http://www.w3.org/2001/XMLSchema#string" MustBePresent="true"></AttributeDesignator>
            </Apply>
            <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">admin_customers</AttributeValue>
         </Apply>
      </Condition>
   </Rule>
   <Rule Effect="Permit" RuleId="primary-group-emps-rule">
      <Target>
         <AnyOf>
            <AllOf>
               <Match MatchId="urn:oasis:names:tc:xacml:1.0:function:string-regexp-match">
                  <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">http://localhost:8280/services/Customers/getEmployee</AttributeValue>
                  <AttributeDesignator AttributeId="urn:oasis:names:tc:xacml:1.0:resource:resource-id" Category="urn:oasis:names:tc:xacml:3.0:attribute-category:resource" DataType="http://www.w3.org/2001/XMLSchema#string" MustBePresent="true"></AttributeDesignator>
               </Match>
               <Match MatchId="urn:oasis:names:tc:xacml:1.0:function:string-equal">
                  <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">read</AttributeValue>
                  <AttributeDesignator AttributeId="urn:oasis:names:tc:xacml:1.0:action:action-id" Category="urn:oasis:names:tc:xacml:3.0:attribute-category:action" DataType="http://www.w3.org/2001/XMLSchema#string" MustBePresent="true"></AttributeDesignator>
               </Match>
            </AllOf>
         </AnyOf>
      </Target>
      <Condition>
         <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-equal">
            <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
               <AttributeDesignator AttributeId="group" Category="urn:oasis:names:tc:xacml:3.0:example-group" DataType="http://www.w3.org/2001/XMLSchema#string" MustBePresent="true"></AttributeDesignator>
            </Apply>
            <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">admin_emps</AttributeValue>
         </Apply>
      </Condition>
   </Rule>
   <Rule Effect="Deny" RuleId="deny-rule"></Rule>
</Policy>
'''