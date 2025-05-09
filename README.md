# WebCrawler

A simple Java-based web crawler that recursively visits a start URL up to a specified depth, restricts crawling to allowed domains, and extracts all headings (`<h1>` to `<h6>`) from each visited page. The result is saved in a  Markdown report.

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
./gradlew run --args="https://example.com 2 example.com"
```

- `<url>`: The starting URL (e.g. `https://example.com`)
- `<depth>`: Maximum crawling depth
- `<allowedDomains>`: Comma-separated list of allowed domains (e.g. `example.com,example.org`)

## Output

The crawler generates a Markdown file named **`report.md`** in the same directory. It logs visited links, their depth, and page headings.

Example content:

```markdown
<br> link to <a>https://example.com</a>
<br>depth: 0
# Main Heading
## Subheading

<br>--> broken link <a>https://example.com/404</a>

<br>--> link to <a>https://example.com/subpage</a>
<br>depth: 1
# Heading on subpage
```

