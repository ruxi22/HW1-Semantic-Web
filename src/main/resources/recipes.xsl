<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

  <xsl:param name="userSkill">Intermediate</xsl:param>

  <xsl:output method="html" version="1.0" encoding="UTF-8" indent="yes" omit-xml-declaration="yes"/>

  <xsl:template match="/recipes">
    <div class="xsl-container" style="background: #f5f5f5; min-height: 100vh; padding: 30px 20px;">
      <style>
          * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
          }

           .xsl-container {
             font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
           }

           .xsl-content {
             max-width: 1200px;
             margin: 0 auto;
           }

           h1 {
             color: #333;
             text-align: center;
             margin-bottom: 10px;
             font-size: 3em;
             text-shadow: none;
           }

           .info {
             color: #555;
             text-align: center;
             margin-bottom: 30px;
             font-size: 1.3em;
           }

          .legend {
            background: white;
            border-radius: 10px;
            padding: 20px;
            margin-bottom: 30px;
            display: flex;
            justify-content: center;
            gap: 40px;
            box-shadow: 0 4px 6px rgba(0,0,0,0.1);
          }

          .legend-item {
            display: flex;
            align-items: center;
            gap: 10px;
          }

          .legend-box {
            width: 30px;
            height: 30px;
            border-radius: 5px;
            border: 2px solid #333;
          }

          .legend-yellow {
            background-color: #FFD700;
          }

          .legend-green {
            background-color: #90EE90;
          }

          .legend-text {
            font-weight: 600;
            color: #333;
          }

          .recipes-grid {
            display: grid;
            grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
            gap: 20px;
            margin-bottom: 30px;
          }

          .recipe-card {
            border-radius: 10px;
            padding: 20px;
            box-shadow: 0 4px 6px rgba(0,0,0,0.2);
            transition: transform 0.3s ease, box-shadow 0.3s ease;
            border: 3px solid #333;
          }

          .recipe-card:hover {
            transform: translateY(-5px);
            box-shadow: 0 8px 12px rgba(0,0,0,0.3);
          }

          .recipe-id {
            display: inline-block;
            background: #333;
            color: white;
            padding: 5px 10px;
            border-radius: 5px;
            font-size: 0.9em;
            font-weight: bold;
            margin-bottom: 10px;
          }

           .recipe-title {
             font-size: 1.7em;
             font-weight: bold;
             margin: 15px 0 10px 0;
             color: #333;
           }

          .recipe-image {
            width: 100%;
            height: 200px;
            object-fit: cover;
            border-radius: 5px;
            margin-bottom: 15px;
            border: 2px solid #999;
          }

          .recipe-section {
            margin-bottom: 15px;
          }

           .recipe-section-title {
             font-weight: bold;
             color: #333;
             font-size: 1.1em;
             margin-bottom: 8px;
             text-transform: uppercase;
             letter-spacing: 1px;
           }

           .tags {
             display: flex;
             flex-wrap: wrap;
             gap: 8px;
           }

           .tag {
             background: #f0f0f0;
             color: #333;
             padding: 8px 14px;
             border-radius: 20px;
             font-size: 1em;
             border: 1px solid #ccc;
           }

          .tag-cuisine {
            background: #E8F4F8;
            border-color: #4A90E2;
            color: #2C5AA0;
          }

          .tag-difficulty {
            background: #FFF8E1;
            border-color: #FFB300;
            color: #FF8C00;
          }

           .stats {
             text-align: center;
             color: #333;
             padding: 25px;
             background: white;
             border-radius: 10px;
             font-size: 1.3em;
             border: 2px solid #ddd;
           }

           .stats-number {
             font-size: 2em;
             font-weight: bold;
             color: #4a7c7e;
           }
        </style>
      <div class="xsl-content">
        <h1>Recipe Collection</h1>
        <div class="info">
          <p>Displaying recipes using XSL Transformation</p>
        </div>

        <div class="legend">
          <div class="legend-item">
            <div class="legend-box legend-yellow"></div>
            <span class="legend-text">Matches User's Skill Level</span>
          </div>
          <div class="legend-item">
            <div class="legend-box legend-green"></div>
            <span class="legend-text">Other Recipes</span>
          </div>
        </div>

        <div class="recipes-grid">
          <xsl:apply-templates select="recipe"/>
        </div>

        <div class="stats">
          <p>Total Recipes Displayed: <span class="stats-number"><xsl:value-of select="count(recipe)"/></span></p>
        </div>
      </div>
    </div>
  </xsl:template>

  <xsl:template match="recipe">
    <xsl:variable name="hasSkillMatch">
      <xsl:choose>
        <xsl:when test="difficultyLevels/difficultyLevel = $userSkill">yes</xsl:when>
        <xsl:otherwise>no</xsl:otherwise>
      </xsl:choose>
    </xsl:variable>

    <div>
      <xsl:attribute name="class">
        <xsl:choose>
          <xsl:when test="$hasSkillMatch = 'yes'">recipe-card</xsl:when>
          <xsl:otherwise>recipe-card</xsl:otherwise>
        </xsl:choose>
      </xsl:attribute>
      <xsl:attribute name="style">
        <xsl:choose>
          <xsl:when test="$hasSkillMatch = 'yes'">background-color: #FFD700; border-color: #FFB300;</xsl:when>
          <xsl:otherwise>background-color: #90EE90; border-color: #228B22;</xsl:otherwise>
        </xsl:choose>
      </xsl:attribute>

      <span class="recipe-id">
        <xsl:value-of select="@id"/>
      </span>

      <xsl:if test="image">
        <img class="recipe-image" src="{image}" alt="{title}"/>
      </xsl:if>

      <div class="recipe-title">
        <xsl:value-of select="title"/>
      </div>

      <div class="recipe-section">
        <div class="recipe-section-title">Cuisine Types</div>
        <div class="tags">
          <xsl:for-each select="cuisineTypes/cuisineType">
            <span class="tag tag-cuisine">
              <xsl:value-of select="."/>
            </span>
          </xsl:for-each>
        </div>
      </div>

      <div class="recipe-section">
        <div class="recipe-section-title">Difficulty Levels</div>
        <div class="tags">
          <xsl:for-each select="difficultyLevels/difficultyLevel">
            <span class="tag tag-difficulty">
              <xsl:value-of select="."/>
            </span>
          </xsl:for-each>
        </div>
      </div>

       <div class="recipe-section" style="margin-top: 15px;">
         <xsl:choose>
           <xsl:when test="$hasSkillMatch = 'yes'">
             <span style="color: #228B22; font-weight: bold; font-size: 1.05em;">Matches user's skill level</span>
           </xsl:when>
           <xsl:otherwise>
             <span style="color: #666; font-size: 1.05em;">Other recipe</span>
           </xsl:otherwise>
         </xsl:choose>
       </div>
    </div>
  </xsl:template>

</xsl:stylesheet>

