# Fine grained security with XACML
Ok I start with XACML. I will use version 3.0. Not sure about who is implementing this version, it seems most famous ones (JBoss for example) stick to 2.0 version.
XACML consist in PEP, PAP, PIP, PDP and policies.
 * PEP (Policy Enforcement Point) is where one or more policies condition must be met,
 * PAP (Policy Administration Point) here you manage your policies. Policies aren't immutables, they change as your need changes,
 * PIP (Policy Information Point) is where you collect additional attributes to be evaluated. To trigger a PIP you must use an `Obbligation` in your policy,
 * PDP (Policy Decision Point) is where attributes are evaluated by conditions,
 * policies are conditions, requirements you must met to be able to do something.

## Writing PEP
PEP is where authorization check should be triggered. For now I choose to use CDI AOP to implement PEP.

## Ok I go for Balana
Why? Well is the first XACML framework who provides it's dependencies in an easy way... But can't find docs...
As PEP is boundary of XACML, PAP, PIP and PDP are strictly platform dependent.

## PDP and how to configure them
Balana provides a PDP who accept three types of utility modules: `AttributeFinder`, `PolicyFinder` and `ResourceFinder`. I don't find any need of `ResourceFinder` for now, maybe in future.. `AttributeFinder` and `PolicyFinder` are responsible to resolve requests about additional attributes and retreive policies. If an attribute is required, `AttributeFinder` asks for it at every `AttributeFinderModule` it encapsulate. `PolicyFinder` is responsible to find policies your PDP should use. Policies could be stored in a database, your `PolicyFinder` will find them and load into PDP. So, `AttributeFinder` and `PolicyFinder` are both very important and respectively related to PIP and PAP.

## Policy evaluation
 * Permit:
 * Deny:
 * Indeterminate: means that more than one policy got a target that match the given request and PDP does not know how to handle results.
 * Not Applicable: means that no policy's target is fulfilled by the given request, so PDP has nothing to evaluate against.

### Policy example
XACML does not look easy.

```
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
```

### Attributes
Are the very important data in requests. Attributes are evaluated agains policies to have a response: DENY, PERMIT, NOT_APPLICABLE or INDETERMINATE. Attributes can be uniquely identified by their three mandatory attributes: `Category`, `AttributeId`, `DataType`. To retreive an attribute this way you must use an AttributeDesignator element in your policy.
Categories are these few:
 - urn:oasis:names:tc:xacml:3.0:attribute-category:resource
 - urn:oasis:names:tc:xacml:3.0:attribute-category:action
 - urn:oasis:names:tc:xacml:3.0:attribute-category:environment
 - urn:oasis:names:tc:xacml:1.0:subject-category:access-subject
 - urn:oasis:names:tc:xacml:1.0:subject-category:recipient-subject
 - urn:oasis:names:tc:xacml:1.0:subject-category:intermediary-subject
 - urn:oasis:names:tc:xacml:1.0:subject-category:codebase
 - urn:oasis:names:tc:xacml:1.0:subject-category:requesting-machine
They are pretty self-explanatory.
Attribute's `AttributeId` can be any URI.

When PDP evaluate request and did not find attributes that policy wants to evaluate, then (the Balana `BasicEvaluationCtx` instance) calls for `AttributeFinder`. `AttributeFinder` is given during config of PDP. It encapsulates a collection of `AttributeFinderModule` who are responsible of retreive unspecified but wanted attributes. If you want to pick attributes from an external source (say database) here you go. Write your own `AttributeFinderModule` implementing its `findAttribute` method and add it to the `AttributeFinder` you will use to create the `PDPConfig` element. 
 
### AttributeDesignator
Indicated in policies. Its scope is to retreive an attribute from a request to allow a condition to evaluate it.

## Condition
Condition can be evaluated as true, false or indeterminate.