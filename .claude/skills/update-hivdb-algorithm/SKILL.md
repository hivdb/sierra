# Update HIVDB-HIV1 Algorithm ASI File

## Overview

This skill guides you through updating the HIVDB-HIV1 algorithm ASI (Algorithm Specification Interface) XML file with new scores, drugs, drug classes, and/or comments. The updates primarily affect the `hivfacts/` submodule.

## Prerequisites

Before starting, verify these tools are available. If any are missing, stop and ask the user to install them.

1. **Node.js** (via nvm): Required to run the asiface tools.
   ```bash
   node --version  # must be available
   ```

2. **Python 3 + uv**: Required to read .xlsx input files via openpyxl.
   ```bash
   uv --version    # must be available
   ```

3. **asiface**: The ASI manipulation toolkit. Check the default location first; if not found, clone it automatically.
   ```bash
   ls ~/gitrepo/asiface/bin/apply-rules.js 2>/dev/null || \
     git clone https://github.com/hivdb/asiface.git ~/gitrepo/asiface
   ```
   If the default `~/gitrepo/asiface` location is not writable or the user prefers elsewhere, ask them where to clone it.

4. **dos2unix**: Required to normalize line endings in generated files.
   ```bash
   dos2unix --version 2>/dev/null || brew install dos2unix
   ```

5. **Docker**: Required to build the Sierra image and run tests. The Docker VM (Colima) must have at least **8GB memory** and **4 CPUs** for regression tests. Check with `colima list`; resize if needed:
   ```bash
   docker --version  # must be available
   colima list        # check memory — must be ≥8GiB for regression tests
   # If memory is too low:
   # colima stop && colima start --cpu 4 --memory 8
   ```

6. **openpyxl** (Python, in a temporary venv): Set up on first use:
   ```bash
   uv venv /tmp/xlsx-env
   uv pip install --python /tmp/xlsx-env/bin/python openpyxl
   ```
   Then use `uv run --python /tmp/xlsx-env/bin/python <script.py>` for all Python/xlsx operations. The venv is ephemeral and can be recreated if needed.

## Execution Rules

### Write Python scripts to files, never use heredocs or `-c`
When running Python code, **always write it to a `.py` file first** (e.g., `/tmp/hivdb_<step>.py`) and then run it with `uv run --python /tmp/xlsx-env/bin/python /tmp/hivdb_<step>.py`. Do NOT pass code via heredoc (`<< 'EOF'`), `python -c "..."`, or inline strings. These cause repeated permission prompts and are harder to debug.

### Normalize line endings
Generated CSV files and ASI XML files may have inconsistent line endings. After writing any output file, run `dos2unix` and `mac2unix` (or `sed`) to ensure Unix line endings (LF only):
```bash
dos2unix /tmp/hivdb_new_comments.csv
dos2unix /tmp/hivdb_new_rules_NRTI.csv
dos2unix /tmp/hivdb_new.xml
```
This is especially important before running `diff -u` comparisons -- mismatched line endings are the most common cause of spurious diff output.

## Key Paths

- **ASI XML files**: `hivfacts/data/algorithms/HIVDB_<VERSION>.xml`
- **Latest symlink**: `hivfacts/data/algorithms/HIVDB_latest.xml` (points to latest version)
- **Version registry**: `hivfacts/data/algorithms/versions.yml`
- **ASI tools**: `~/gitrepo/asiface/bin/` (default; may differ -- see Prerequisites)

## Available Tools (asiface/bin)

| Tool | Purpose | Key Params |
|------|---------|------------|
| `extract-comments.js` | Extract comments from ASI to CSV | `--input-asi`, `--comment-csv` |
| `extract-rules.js` | Extract scoring rules from ASI to CSV | `--input-asi`, `--drugclass`, `--rule-csv` |
| `apply-comments.js` | Apply comment CSV back into ASI | `--comment-csv`, `--input-asi`, `--output-asi` |
| `apply-rules.js` | Apply rule CSV into ASI for a drug class | `--drugclass`, `--rule-csv`, `--input-asi`, `--output-asi` |

## Internal CSV Formats (used by asiface tools)

### Comments CSV
Columns: `id, gene, drugClass, text, sortTag, conditionType, condition, date`

- `conditionType`: `MUTATION` or `DRUGLEVEL`
- `id`: gene prefix + position + AAs (freeform, e.g., `IN155H`, `RT65RN`, `CA67HYNK`) or drug + level name for DRUGLEVEL (e.g., `DTGHigh`). Follow the established naming pattern of existing IDs in the CSV.
- `condition`: Must adhere to **ASI syntax**. For MUTATION type: position number followed by amino acid single-letter codes, with `i` for insertion and `d` for deletion (e.g., `155H`, `65RN`, `67HYNK`, `69i`, `68d`, `118ACDEFHIKLMNPQSTVWYid`). For DRUGLEVEL type: `<DRUG>=<LEVEL>` (e.g., `DTG=5`).
- `date`: `YYYY-MM-DD`

### Rules CSV
Columns: `rule, <DrugName> Score, ...` (one column per drug)

- `rule`: condition text like `65R`, `41L AND 215FY`
- Score columns: integer penalty scores

## Input Spreadsheet Formats (from user)

The user provides Excel (.xlsx) files. These come in several variant formats. You MUST handle all of them. Use `uv venv /tmp/xlsx-env && uv pip install --python /tmp/xlsx-env/bin/python openpyxl` then `/tmp/xlsx-env/bin/python` to read .xlsx files.

### Important: Multi-tab Workbooks

A single .xlsx file may contain **both scores and comments** across different tabs/sheets, and may even cover **multiple drug classes**. Do NOT assume one file = one type of change.

**Always start by listing all sheet names** in every provided file and classifying each sheet by its content:
- Sheets with drug score columns (drug names as headers like `ABC`, `AZT`, `BIC`, `DRV/r`, etc.) are **score sheets**
- Sheets with `Comment`, `Condition`, or `Mutation Type` columns are **comment sheets**
- Sheet names often hint at content: e.g., "OriginalScores", "RevisedComments", "NRTI_Original", "Nov2025Updates"
- A sheet may have both an "Original" and "Updated/Revised" counterpart -- pair them by naming convention

After classifying, process each sheet pair according to its type (score or comment) using the appropriate rules below.

### Score Spreadsheets

Score spreadsheets have two logical tables side-by-side (separated by an empty column):
- **Left table**: Single-mutation rules — columns: `Rule, <Drug1>, <Drug2>, ...`
- **Right table**: Combination rules — columns: `Combination Rule, <Drug1>, <Drug2>, ...`

**Format variants observed:**

1. **Original + Revised sheets** (e.g., `NRTI_Update_April2026.xlsx`):
   - Sheet "OriginalScores" has the old scores; sheet "RevisedScores" has the new scores
   - New drugs may appear as extra columns in the Revised sheet (e.g., "ISL" added)
   - Compare both sheets to find changes

2. **Original + Updates sheets** (e.g., `NRTI_Scores_Nov2025.xlsx`, `PI_Scores.xlsx`):
   - Sheet "Original"/"NRTI_Original" has old scores; sheet "Updates"/"NovUpdates" has new scores
   - Changes indicated by `=>` notation in cell values: `'10=>5'`, `'45=>30'`, `'0=>5'`
   - Some rows may be added/removed between sheets
   - Some combo rules may have text like `'L74FIM + G118R => L74FM + G118R'` (rule text changed)
   - A cell or trailing column may say `'Delete'` to indicate removal of a rule

3. **Two comparison sheets** (e.g., `ScoresNNRTI_1565816029373.xlsx`):
   - Sheet "ScoresNNRTI_Edits" has new scores; another sheet has old scores
   - Extra columns `Position`, `AA` may be present (ignore them, use `Rule` column)
   - No `=>` markers -- you must diff cell-by-cell

**Score parsing rules:**
- Cells with `=>` (e.g., `'10=>5'`, `'0=>5'`): left side is old value, right side is new value
- Integer cells: the score value (0 means no penalty; negative scores like `-10` are valid)
- `None`/empty cells: 0 (no penalty)
- Rule column cells with `=>`: the rule condition text itself changed
- Trailing `'Delete'` cells or columns: that rule should be removed

**Zero-score and missing-drug rules:**
- In the standard rules CSV (internal format), a score of 0 must be represented as **empty** (not `0`). The ASI/CSV was previously maintained by hand and may contain literal `0` values -- normalize them all to empty.
- Some drugs in a drug class are no longer actively maintained in input spreadsheets but must be kept in the ASI for backward compatibility. If the input spreadsheet lacks a column for a drug that exists in the current ASI, carry forward that drug's existing scores unchanged.
- If a new rule is added and a drug has no score listed, assume the score is 0 (i.e., empty in the CSV).

### Comment Spreadsheets

Comment spreadsheets contain mutation/position-based comments. Two main formats:

1. **Original + Updates sheets with Old/New columns** (e.g., `NRTI_Comments_2025.xlsx`, `INSTI_Comments_Oct_2025.xlsx`):
   - Sheet "Original"/"Jan2025": columns `Condition, Comment/Mutation Type, Comment`
   - Sheet "NovUpdates"/"Nov2025Updates": columns `Condition, Comment/Mutation Type, Old Comment, New Comment`
   - If "New Comment" is `None`/empty: comment is unchanged (use old)
   - If "New Comment" is `'Delete'`: delete this comment
   - If "New Comment" has text: use it as the replacement
   - `Condition` column: mutation positions like `40F`, `41L`, `44AD`, `65R`, `118R`, or drug levels like `DTG=3`, `CAB=4`
   - `Comment/Mutation Type` column: drug class name (e.g., `NRTI`, `NNRTI`, `INSTI`) or type like `Accessory`, `Other`, or `None` for DRUGLEVEL comments

2. **Simple Original + Revised sheets** (e.g., `Comments_NNRTI_Oct2018.xlsx`):
   - Both sheets have columns: `Condition, Comment/Mutation Type, Comment` (or with `Gene, DrugClass` prefix)
   - Optional extra columns: `Gene`, `DrugClass`, `Pos`, `Rank`, `Aas`
   - `Rank` column indicates ordering when multiple comments exist for the same position
   - Catch-all `$listMutsIn` comments appear explicitly with wildcard AAs like `ACDEFGHKMNPQRSTWY_-`
   - Compare text between sheets to find changes

3. **Single sheet with just new comments** (rare):
   - Columns: `Condition, Comment/Mutation Type, Comment`
   - All listed comments are the desired final state for that drug class

**Comment parsing rules:**
- `Condition` values map to the `condition` field in the internal CSV
- The gene and drugClass must be inferred from context (file name, sheet name, or the `Comment/Mutation Type` column)
- Drug class mapping: `NRTI`->gene `RT`, `NNRTI`->gene `RT`, `PI`->gene `PR`, `INSTI`->gene `IN`, `CAI`->gene `CA`
- `Comment/Mutation Type` may say the drug class name, `Accessory`, `Other`, or be empty -- for the internal CSV, always use the actual drug class name in the `drugClass` field
- Comments with type `Accessory` or `Other` are still valid comments for that drug class

## Step-by-Step Procedure

### Phase 1: Information Gathering

Ask the user the following questions (use AskUserQuestion with multiSelect where appropriate):

1. **What types of changes?** (multi-select)
   - Score changes (single-mutation and/or combo-mutation)
   - Comment changes
   - New drug(s)
   - New drug class(es)
   - New gene(s)

2. **What is the desired new version number?** (free text)

3. **Input files**: Ask the user to provide file paths for the spreadsheet(s) containing the changes. Files are typically .xlsx. A single file may contain both scores and comments in different tabs, and may cover multiple drug classes. The user may provide one file per drug class, one file for everything, or any combination. After receiving the files, **list all sheet names in each file** to classify contents before proceeding.

4. **If adding new drug(s)**: Ask for each drug's short name (e.g., `LEN`) and full name (e.g., `lenacapavir`). Follow the same case format as existing drugs in the ASI (short name: UPPERCASE or with `/r` suffix for boosted PIs; full name: lowercase).

5. **If adding new drug class(es)**: Ask for the class name (e.g., `CAI`) and which gene it belongs to.

6. **If adding new gene(s)**: Ask for the gene name (e.g., `CA`), abstract gene name, reference sequence, reference ranges, strain, and mutation types. The ASI XML will need a new `<GENE_DEFINITION>` block, and `genes_hiv1.yml` will need a new entry.

### Phase 2: Extract Current State

Find the latest ASI file via the symlink:
```bash
readlink hivfacts/data/algorithms/HIVDB_latest.xml
```

Extract current data for comparison:
```bash
# Extract comments
node ~/gitrepo/asiface/bin/extract-comments.js \
  --input-asi hivfacts/data/algorithms/HIVDB_latest.xml \
  --comment-csv /tmp/hivdb_current_comments.csv

# Extract rules for each affected drug class
node ~/gitrepo/asiface/bin/extract-rules.js \
  --input-asi hivfacts/data/algorithms/HIVDB_latest.xml \
  --drugclass <DRUGCLASS> \
  --rule-csv /tmp/hivdb_current_rules_<DRUGCLASS>.csv
```

### Phase 3: Process Changes

#### For Score Changes

1. Read the user's input score spreadsheet using openpyxl (install in `/tmp/xlsx-env` if needed).
2. Identify the format variant (see "Score Spreadsheets" above) by inspecting sheet names and cell patterns.
3. Parse both single-mutation rules (left table) and combo-mutation rules (right table).
4. **Always diff every cell** against the current extracted rules CSV, regardless of whether `=>` markers are present. For cells with `=>` notation, extract the NEW value (right side) and verify the OLD value (left side) matches the current ASI. For cells without `=>`, compare the value directly to the current ASI.
5. Report **all discordances**: if a cell has no `=>` but its value differs from the current ASI, report it. If a cell has `=>` but the old side doesn't match the current ASI, report it. The user needs to see every unexpected difference.
6. Handle rule additions, deletions, and rule text changes:
   - **Additions**: rules present in the new sheet but not in the current ASI
   - **Deletions**: rules marked with `Delete`, OR rules present in the current ASI but missing from the new sheet -- both count as deletions. Report missing rules to the user for confirmation.
   - **Rule text changes**: combo rules may have `=>` in the rule name (e.g., `L74FIM + G118R => L74FM + G118R`), but rule text may also change silently (no `=>` marker). Always compare rule text against the current ASI and **report any rule text differences** to the user for confirmation, even if unmarked.
7. Produce updated rule CSVs in the internal format (one per affected drug class). The CSV must include ALL rules for the drug class (unchanged + changed), as `apply-rules` replaces everything.
8. Save to working paths (e.g., `/tmp/hivdb_new_rules_<DRUGCLASS>.csv`).

#### For Comment Changes

1. Read the user's input comment spreadsheet using openpyxl.
2. Read the extracted current comments CSV (`/tmp/hivdb_current_comments.csv`).
3. Identify the format variant (see "Comment Spreadsheets" above).
4. Determine which drug class(es) are affected.
5. Create an updated comments CSV that:
   - Preserves the **original order** of comments
   - Updates text for modified comments (where "New Comment" column has text, or where Revised sheet differs from Original)
   - Updates the `id` and `condition` fields if mutations in the condition changed
   - Updates the `date` field ONLY if there is an actual content change (not for typo fixes or space trimming)
   - Trims redundant inner/outer spaces
   - Fixes encoding errors: unify `naive` to `naïve`, fix other encoding artifacts
   - **Deletes** comments marked with `'Delete'` in the input, or that no longer appear in the Revised sheet for touched drug classes.
   - **CRITICAL: `$listMutsIn{...}` catch-all comments**: These are companion rows in the internal CSV for main mutation comments. A single position (e.g., RT100) may have **multiple main comments** (e.g., RT100I, RT100V) and one catch-all (e.g., RT100ACDEF...id). The catch-all typically contains condensed versions of the main comments' text plus a `$listMutsIn{...}` sentence at the end. They will likely NOT appear in the input spreadsheet. Rules:
     - **Multiple main comments per position**: A catch-all aggregates text from all main comments at that position. The existing catch-all establishes the pattern for how sentences are ordered, which main comments are included, and how they are abbreviated. **Respect the existing pattern** -- do not add sentences for main comments that were previously omitted or expand shortened sentences.
     - **Sentence ordering**: Main comment sentences in the catch-all are ranked by importance (most important first). Follow the existing order; do not re-rank.
     - If a main comment is **updated**, update the corresponding sentence(s) in the catch-all to match, preserving the existing abbreviation/omission pattern.
     - If a main comment is **deleted**: remove its corresponding sentence(s) from the catch-all. Only **delete the entire catch-all** if ALL main comments for that position are deleted.
     - If a main comment is **unchanged**, keep its portion in the catch-all unchanged.
     - If a main comment still exists but the catch-all is missing from input, **keep the catch-all** -- do NOT delete it just because it's not in the input.
     - **Never create new catch-all comments.** Only maintain existing ones. If a position has main comments but no existing catch-all, leave it that way.
   - For DRUGLEVEL (result) comments: update them if they appear in the input, delete if marked `'Delete'` or if the drug class's comment list in the input no longer includes them.
6. **Update ALL comments of every touched drug class** -- even those not explicitly changed, ensure they are carried forward. Comments for untouched drug classes must be passed through unchanged.
7. Save the new comments CSV to a working path (e.g., `/tmp/hivdb_new_comments.csv`).

#### For New Drug(s)

Edit the ASI XML directly to add:
- Add drug short name to the `<DRUGLIST>` of the appropriate `<DRUGCLASS>` in `<DEFINITIONS>`
- Add a new `<DRUG>` block with `<NAME>`, `<FULLNAME>`, and `<RULE>` sections (rules come from the score spreadsheet)

#### For New Drug Class(es)

Edit the ASI XML directly to add:
- A new `<GENE_DEFINITION>` or update existing one's `<DRUGCLASSLIST>`
- A new `<DRUGCLASS>` block with `<NAME>` and `<DRUGLIST>`

### Phase 4: Review with User (CRITICAL)

Before applying changes, show the user a detailed diff for review. **Check three times, be super careful, don't miss any changes.**

1. For comment changes:
   ```bash
   diff -u /tmp/hivdb_current_comments.csv /tmp/hivdb_new_comments.csv > /tmp/hivdb_comments_diff.txt
   ```
   - Render the diff in a readable format for the user
   - Also save the raw diff file for reference

2. For score/rule changes (per drug class):
   ```bash
   diff -u /tmp/hivdb_current_rules_<DRUGCLASS>.csv /tmp/hivdb_new_rules_<DRUGCLASS>.csv > /tmp/hivdb_rules_<DRUGCLASS>_diff.txt
   ```
   - Render each diff readably
   - Save raw diffs

3. Present all diffs to the user and wait for explicit confirmation before proceeding.

### Phase 5: Generate New ASI File

Once the user confirms:

1. Start with a copy of the current ASI as the base:
   ```bash
   cp hivfacts/data/algorithms/HIVDB_latest.xml /tmp/hivdb_new.xml
   ```

2. If new drugs or drug classes need to be added, edit `/tmp/hivdb_new.xml` directly first (add `<DRUGCLASS>`, `<DRUGLIST>`, `<DRUG>` blocks).

3. Apply rules (one command per affected drug class):
   ```bash
   node ~/gitrepo/asiface/bin/apply-rules.js \
     --drugclass <DRUGCLASS> \
     --rule-csv /tmp/hivdb_new_rules_<DRUGCLASS>.csv \
     --input-asi /tmp/hivdb_new.xml \
     --output-asi /tmp/hivdb_new.xml
   ```

   **Note:** `apply-rules` regenerates the entire drug class and may reorder rules within MAX() groups and reposition rules within each drug block. This is cosmetic -- the scores are unchanged and the ASI engine evaluates all rules regardless of order.

4. Apply comments:
   ```bash
   node ~/gitrepo/asiface/bin/apply-comments.js \
     --comment-csv /tmp/hivdb_new_comments.csv \
     --input-asi /tmp/hivdb_new.xml \
     --output-asi /tmp/hivdb_new.xml
   ```

5. **Manually update the version** in the ASI XML:
   - `<ALGVERSION>` to the new version number
   - `<ALGDATE>` to today's date

### Phase 6: Install New ASI File

1. Copy the new ASI file into place:
   ```bash
   cp /tmp/hivdb_new.xml hivfacts/data/algorithms/HIVDB_<NEW_VERSION>.xml
   ```

2. Update the symlink:
   ```bash
   cd hivfacts/data/algorithms && ln -sf HIVDB_<NEW_VERSION>.xml HIVDB_latest.xml
   ```

3. Update `versions.yml` -- add the new version entry at the end of the `HIVDB:` list:
   ```yaml
     - ["<NEW_VERSION>", "<YYYY-MM-DD>", "HIV1"]
   ```

4. **Do NOT update `version.json`** -- it will be handled automatically by `make data` in Phase 7.

### Phase 7: Update Data Files

After the ASI file is installed, update the supporting data files under `hivfacts/data/`. These files feed the web application and API. JSON files are auto-generated from their CSV/YML sources via `make data`.

#### Key Data Files

| File | Format | Auto-generates | When to update |
|------|--------|----------------|----------------|
| `conditional-comments_hiv1.csv` | Same as asiface comment CSV | `conditional-comments_hiv1.json` | Every version (comments changed) |
| `drugs.yml` | YAML | `drugs.json` | New drug added |
| `drug-classes_hiv1.yml` | YAML | `drug-classes_hiv1.json` | New drug class added |
| `genes_hiv1.yml` | YAML | `genes_hiv1.json` | New gene added |
| `mutation-type-pairs_hiv1.csv` | CSV | `mutation-type-pairs_hiv1.json` (via drms pipeline) | New scored positions/mutations |
| `algorithms/versions.yml` | YAML | `algorithms/versions.json` | Already done in Phase 6 |

#### 7.1: Update `conditional-comments_hiv1.csv`

This file has the **exact same format** as the asiface comment CSV (`id,gene,drugClass,text,sortTag,conditionType,condition,date`). Copy the new comments CSV generated in Phase 5:

```bash
cp /tmp/hivdb_new_comments.csv hivfacts/data/conditional-comments_hiv1.csv
dos2unix hivfacts/data/conditional-comments_hiv1.csv
```

Verify the header line matches: `id,gene,drugClass,text,sortTag,conditionType,condition,date`.

#### 7.2: Update `drugs.yml` (if new drug added)

Add an entry for each new drug in `hivfacts/data/drugs.yml`. Follow the existing format and grouping (drugs are grouped by drug class with blank lines between groups):

```yaml
- name: <SHORT_NAME>
  fullName: <full_name>
  displayAbbr: <DISPLAY_ABBR>
  drugClass: <DRUG_CLASS>
```

- `name`: UPPERCASE short name (e.g., `ISL`)
- `fullName`: lowercase full name (e.g., `islatravir`)
- `displayAbbr`: display abbreviation, usually same as `name` (e.g., `ISL`). For boosted PIs, includes `/r` suffix (e.g., `DRV/r`).
- `drugClass`: the drug class name (e.g., `NRTI`)
- Optional `synonyms` list if the drug has alternate names in other algorithms

Place the new drug entry within its drug class group, maintaining alphabetical or logical ordering consistent with existing entries.

#### 7.3: Update `drug-classes_hiv1.yml` (if new drug class added)

Add an entry to `hivfacts/data/drug-classes_hiv1.yml`:

```yaml
- name: <CLASS_NAME>
  ordinal: <next_number>
  abstractGene: <GENE>
  fullName: <Full Class Name>
  strains:
    - HIV1
  synonyms: []
  mutationTypes:
    - Major
    - Accessory
    - Other
```

The `ordinal` should be the next sequential number. The `mutationTypes` list defines what mutation type categories are valid for this drug class. For most classes it's `Major, Accessory, Other`. For NRTI/NNRTI it uses the drug class name instead of `Major` (e.g., `NRTI, Other`).

#### 7.4: Update `genes_hiv1.yml` (if new gene added)

Add an entry to `hivfacts/data/genes_hiv1.yml` following the existing format. Each gene entry requires:
- `name`: strain prefix + gene (e.g., `HIV1CA`)
- `ordinal`: sequential number
- `strain`: `HIV1`
- `abstractGene`: short gene name (e.g., `CA`)
- `refSequence`: reference amino acid sequence (quoted multi-line string)
- `refRanges`: nucleotide position ranges in the HXB2 genome (e.g., `[[1186, 1878]]`)
- `mutationTypes`: list of valid mutation type categories
- `strainModifiers`: usually `{}`

Also add a corresponding `<GENE_DEFINITION>` block in the ASI XML with `<NAME>` and `<DRUGCLASSLIST>`.

#### 7.5: Update `mutation-type-pairs_hiv1.csv` (if new scored positions/mutations)

This CSV maps mutations to their types. Columns: `strain,gene,drugClass,position,aas,mutationType,isUnusual`

When new scoring rules are added for previously unscored positions, or new comments reference new positions, add corresponding entries. The input comment spreadsheet often includes a `Comment/Mutation Type` column that indicates the mutation type.

**Format rules:**
- `strain`: always `HIV1`
- `gene`: gene short name (`RT`, `PR`, `IN`, `CA`)
- `drugClass`: the drug class name
- `position`: amino acid position number
- `aas`: single-letter amino acid codes for this entry (can be multiple, e.g., `FY`)
- `mutationType`: the type -- drug class name (e.g., `NRTI`), `Major`, `Accessory`, or `Other`
- `isUnusual`: `TRUE` for catch-all/wildcard entries (all other AAs at a position), `FALSE` for specific scored mutations

**Typical pattern for a new position** (e.g., position 114 with S scored as NRTI):
```
HIV1,RT,NRTI,114,S,NRTI,FALSE
HIV1,RT,NRTI,114,ACDEFGHIKLMNPQRTVWY_-,Other,TRUE
```

The catch-all entry covers all amino acids NOT explicitly listed at that position for the drug class, with `isUnusual=TRUE`. Use `_` for stop codon and `-` for deletion. The wildcard AAs should exclude the reference amino acid AND any explicitly scored AAs.

**Discordance checking:** Compare mutation types from the input spreadsheet against existing entries. Report any discordances to the user for confirmation before making changes.

#### 7.6: Run `make data`

After all data files are updated, generate the JSON files:

```bash
cd hivfacts && make data
```

This runs `pipenv run python` scripts to convert:
- YAML files → JSON files
- `conditional-comments_hiv1.csv` → `conditional-comments_hiv1.json`
- `versions.yml` → `versions.json`
- ASI XML → `drms_hiv1.csv` (drug resistance mutations list)

Verify no errors in the output. If `pipenv` is not set up, follow the project's setup instructions.

### Phase 8: Build Docker Image and Run Tests

After all data files are updated, build the Docker image and run the test suite to verify everything works.

#### 8.1: Sync hivfacts resources

The Makefile `sync-hivfacts` target copies `hivfacts/data/*` into `hivfacts/hivfacts-java/src/main/resources` for the Java build:

```bash
make sync-hivfacts
```

#### 8.2: Build the Docker image

Build the full Sierra Docker image from the project root (`~/gitrepo/sierra`):

```bash
make build
```

This produces the `hivdb/sierra:latest` image. The Dockerfile is a multi-stage build:
- **Stage 1** (`dependencies-installer`): Caches Gradle dependencies
- **Stage 2** (`postalign-builder`): Builds the postalign Python tool
- **Stage 3** (`builder`): Compiles all Java source, builds the WAR, compiles minimap2, and includes postalign
- **Stage 4** (runtime): Installs Tomcat, copies WAR/minimap2/postalign into a JRE-based image

#### 8.3: Run tests

The Makefile provides targets for building the builder image and running tests:

```bash
make test
```

This builds the builder image (`docker build --target builder`) and runs `gradlew test` inside it with the project mounted. The builder image includes JDK, Gradle, minimap2, postalign, and Python 3.11, so all tests including aligner-dependent ones will run.

To include regression tests (slow, skipped by default):

```bash
make test-regression
```

**IMPORTANT: Working directory quirk.** Claude Code's shell may silently change `pwd` (e.g., after `cd` into a submodule). Always use `pushd`/`popd` instead of `cd` to avoid losing track of the working directory. The Makefile uses `$(shell pwd)` for the volume mount, so it must be run from the sierra root.

**IMPORTANT: Redirect test output.** When running docker commands directly (not via Makefile), redirect output to a file under the project's `local/` folder and read from it, rather than piping through `grep` or other filters. The `local/` folder is gitignored and is the designated place for local working files.

```bash
make test > local/test-output.txt 2>&1; echo "exit: $?"
```

Then read the output:
```bash
grep "FAILED\|tests completed" local/test-output.txt
```

For detailed error messages, check the JUnit XML results:
```bash
grep 'message=' sierra-tests/build/test-results/test/TEST-*.xml
```

#### 8.5: Categorize and fix test failures

Test failures from algorithm updates fall into one category: hardcoded expected values in tests that no longer match. Fix them to reflect the new algorithm data.

| Test class | Test | Typical cause | What to update |
|------------|------|---------------|----------------|
| `VirusTest` | `testGetLatestDrugResistAlgorithm`, `testGetDefaultDrugResistAlgorithm` | Algorithm version changed | Update expected version string (e.g., `"10.1"` → `"10.2"`) |
| `DrugTest` | `testLoadJSON`, `testFullDrugs` | New drug added | Update expected drug count (e.g., `27` → `28`) |
| `DrugClassTest` | `testGetDrugsFullDrugClass` | New drug added to a class | Update expected drug list (e.g., add `ISL` to NRTI list) |
| `HIVTest` | `testGetDrugs` | New drug added | Update expected drug count |
| `HIVTest` | `testGetDrugResistAlgorithms` | New algorithm version | Update expected algorithm count (e.g., `51` → `52`) |
| `HIVTest` | `testGetMutationTypePairs` | New mutation-type entries | Update expected pair count (e.g., `265` → `267`) |
| `HIV2Test` | `testGetDrugs` | New drug added (shared drug list) | Update expected drug count |
| `GenePositionTest` | `testGetDRGenePositions` | New scored position | Update expected position count (e.g., `92` → `93`) |
| `GeneDRASIResultHandlerDrugSuscTest` | `testGetTotalDrugScores` | New drug has scores for test mutations | Add new drug to expected score map |
| `GeneDRASIResultHandlerDrugSuscTest` | `testDrugSuscGetPartialScores` | New drug has partial scores | Add new drug's partial scores to expected map |
| `GeneDRASIResultHandlerDrugSuscTest` | `testDrugSuscGetStatement` | New drug gets a resistance statement | Add new drug's statement to expected map |

To fix these, read the failing test source, find the hardcoded assertion, and update it to match the actual (new correct) value shown in the test failure message. After fixing, re-run the tests to confirm only Category 1 and 2 failures remain.

#### 8.6: Regenerate regression test expectations (MANDATORY)

Two regression tests compare output against expected-result files that **must** be regenerated after every algorithm update. Gradle tasks are provided in `sierra-tests/build.gradle` for both. Both generators use `TestUtils.writeFile()` which writes output under `sierra-tests/testResult/` (relative to the subproject directory).

**Mutation pattern expectations** (`MutationPatternsResistanceJsonComparisonTest`):

This test compares `MutationPatterns.dumps(algorithm)` output against JSON files in `hivfacts/data/patterns-hiv1/`. The generator regenerates all five files (one per drug class: NRTI, NNRTI, PI, INSTI, CAI).

```bash
docker run --rm \
  -v ~/gitrepo/sierra:/project \
  -w /project \
  hivdb/sierra-builder:latest \
  bash -c '/sierra/gradlew --no-daemon generateMutationPatterns > /project/local/generate-patterns.txt 2>&1; echo "exit: $?"'
```

Copy the results into place:

```bash
cp sierra-tests/testResult/hivfacts/data/patterns-hiv1/patterns-*.json hivfacts/data/patterns-hiv1/
```

Then re-sync hivfacts resources:

```bash
make sync-hivfacts
```

**Algorithm comparison expectations** (`AlgorithmComparisonRegressionTest`):

This test aligns test sequences and compares `AlgorithmComparison.compareAll()` output against `sierra-tests/src/test/resources/AlgorithmComparisonTestExpecteds.json`. The generator invokes the aligner (minimap2 + postalign), both of which are available in the builder image. This generator takes several minutes to run.

```bash
docker run --rm \
  -v ~/gitrepo/sierra:/project \
  -w /project \
  hivdb/sierra-builder:latest \
  bash -c '/sierra/gradlew --no-daemon generateAlgorithmComparison > /project/local/generate-algcomp.txt 2>&1; echo "exit: $?"'
```

Copy the result:

```bash
cp sierra-tests/testResult/src/test/resources/AlgorithmComparisonTestExpecteds.json \
   sierra-tests/src/test/resources/AlgorithmComparisonTestExpecteds.json
```

#### 8.7: Run regression tests (MANDATORY)

After regenerating expectations and fixing unit tests, rebuild the builder and run the full test suite including regression tests:

```bash
make test-regression > local/test-regression.txt 2>&1; echo "exit: $?"
```

If you encounter Gradle lock file errors (`Timeout waiting to lock`), a previous container may have left stale locks. Kill any running containers and clean locks:

```bash
docker kill $(docker ps -q) 2>/dev/null
rm -f .gradle/9.4.1/fileHashes/fileHashes.lock .gradle/buildOutputCleanup/buildOutputCleanup.lock
```

Then retry. All tests must pass (zero failures) before proceeding.

**Important notes:**
- Generator source code: `sierra-tests/src/test/java/.../mutationpattern/MutationPatternExpectedsGenerator.java` and `sierra-tests/src/test/java/.../scripts/AlgorithmComparisonTestExpectedsGenerator.java`
- Generators write to `sierra-tests/testResult/` (the Gradle `JavaExec` task uses the subproject dir as working directory, and `TestUtils.writeFile()` prepends `testResult/`)
- Both generators can run in the builder image (which includes minimap2 and postalign)
- After copying generated pattern files, always re-run `make sync-hivfacts` before rebuilding the builder
- Use `docker top <container-id>` to inspect what a long-running container is doing

## Important Reminders

- Always extract current state first for accurate comparison
- The comments CSV must include ALL comments (not just changed ones) when applying -- the apply tool replaces everything
- The rules CSV replaces all rules for the specified drug class -- include unchanged rules too
- Be meticulous about `$listMutsIn` catch-all comments -- they are easy to accidentally delete
- Score `=>` symbols in input spreadsheets indicate changes but must be verified against current values
- Drug class names: `NRTI`, `NNRTI`, `PI`, `INSTI`, `CAI` (as of v10.1)
- Gene names: `CA`, `PR`, `RT`, `IN`
- Gene-to-drugclass mapping: `CA`->`CAI`, `PR`->`PI`, `RT`->`NRTI,NNRTI`, `IN`->`INSTI`
- When editing the ASI XML directly (for new drugs/classes), follow the exact formatting of existing entries
- Input spreadsheets may use various naming conventions for sheets -- adapt to whatever is provided
- When the score spreadsheet has two side-by-side tables, the empty `None` column is the separator
