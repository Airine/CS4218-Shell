---
name: Bug Report
about: Use this template for bug report in hackathon
title: "[TEAM X/A]: [APPLICATION NAME + BUG NAME]"
labels: bug
assignees: airine
milestone: hackathon
---

# [TEAM X/A]: [APPLICATION NAME + BUG NAME]

## Description

The bug is due to invalid handling of “ symbol [...] Ref. spec page X, Section Y, 

Paragraph Z. Also discussed on the forum on 14/03/2015 [...]

...

...

## Testcase

```java
/**
* The bug is due to invalid handling of “ symbol [...] Ref. spec page X, Section Y,
* Paragraph Z. Also discussed on the forum on 14/03/2015 [...] */
@Test
public void testHasCommand() {
// test code goes here
}
```
Location: `/path/to/testFile`


### Checklist for a bug report:
- [ ] description of the problem in one sentence
- [ ] reference to the part of specification that is violated
- [ ] reference to the failing code 
