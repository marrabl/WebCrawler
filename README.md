# WebCrawler

A simple Java-based web crawler that concurrent visits a URL link up to a specified depth, restricts crawling to allowed domains, and extracts all headings (`<h1>` to `<h6>`) from each visited page. The result is saved in a  Markdown report.

## Running the Program

* Clone Repository:
```sh
git clone https://github.com/marrabl/webcrawler.git
cd webcrawler
```

* Run Program
```sh
./gradlew run --args=<url> <depth> <allowedDomains>
```

**Example:**

```sh
./gradlew run --args="https://example.com 2 example.com,iana.org" 
```

- `<url>`: The starting URL (e.g. `https://example.com`)
- `<depth>`: Maximum crawling depth
- `<allowedDomains>`: Comma-separated list of allowed domains (e.g. `example.com,example.org`)

## Output

The crawler generates a Markdown file named **`report.md`** in the same directory. It logs visited links, their depth, and page headings.

Example content:

```markdown
- [link](https://example.com) (depth: 0)
  Headings:
  # Example Domain


- [link](https://www.iana.org/domains/example) (depth: 1)
Headings:
# Example Domains
## Further Reading


- [link](http://www.iana.org/go/rfc2606) (depth: 2)
Headings:


- [link](http://www.iana.org/domains/int) (depth: 2)
Headings:
# .INT Zone Management
## Maintain
## Policy & Procedures
## Domain Names
```

